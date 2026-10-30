
import { useEffect, useState } from 'react'
import './App.css'

// Define a estrutura de um produto recebido da API
type Produto = {
    id: number
    nome: string
    codigoInterno: string
    gtin: string | null
    precoVenda: number
    ativo: boolean
}

type Tela = 'inicio' | 'produtos' | 'vendas' | 'caixa'

function App() {
    const [telaAtual, setTelaAtual] = useState<Tela>('inicio')

    // Armazena os produtos recebidos do backend
    const [produtos, setProdutos] = useState<Produto[]>([])

    // Guarda o texto digitado na pesquisa
    const [pesquisa, setPesquisa] = useState('')

    // Armazena possíveis erros na consulta
    const [erro, setErro] = useState('')

    // Controla a exibição do formulário de cadastro
    const [mostrarCadastro, setMostrarCadastro] = useState(false)

    // Dados digitados no formulário de cadastro
    const [nomeProduto, setNomeProduto] = useState('')
    const [codigoProduto, setCodigoProduto] = useState('')
    const [gtinProduto, setGtinProduto] = useState('')
    const [precoProduto, setPrecoProduto] = useState('')

    // Armazena o produto selecionado para edição.
    // null significa que nenhum produto está sendo editado.
    const [produtoEmEdicao, setProdutoEmEdicao] = useState<Produto | null>(null)

    // Armazenam os valores digitados no formulário de edição.
    // São separados dos estados utilizados no cadastro de produtos.
    const [nomeEdicao, setNomeEdicao] = useState('')
    const [codigoEdicao, setCodigoEdicao] = useState('')
    const [gtinEdicao, setGtinEdicao] = useState('')
    const [precoEdicao, setPrecoEdicao] = useState('')

    // Executa a consulta quando a aplicação é iniciada
    useEffect(() => {
        async function carregarProdutos() {
            try {
                const resposta = await fetch('/api/produtos')

                if (!resposta.ok) {
                    throw new Error('Erro ao consultar produtos')
                }

                const dados: Produto[] = await resposta.json()

                setProdutos(dados)
            } catch {
                setErro('Não foi possível carregar os produtos.')
            }
        }

        carregarProdutos()
    }, [])

    // Filtra os produtos pelo nome, ignorando maiúsculas e minúsculas
    const produtosFiltrados = produtos.filter((produto) =>
        produto.nome.toLowerCase().includes(pesquisa.toLowerCase())
    )

    async function cadastrarProduto() {
        try {
            const resposta = await fetch('/api/produtos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    nome: nomeProduto,
                    codigoInterno: codigoProduto,
                    gtin: gtinProduto || null,
                    precoVenda: Number(precoProduto),
                    ativo: true,
                }),
            })

            if (!resposta.ok) {
                if (resposta.status === 400) {
                    throw new Error('Dados inválidos. Verifique os campos do produto.')
                }

                if (resposta.status === 409) {
                    throw new Error('Já existe um produto com esse código ou GTIN.')
                }

                throw new Error('Não foi possível cadastrar o produto.')
            }

            const produtoSalvo: Produto = await resposta.json()

            // Atualiza a tabela sem recarregar a página
            setProdutos((anteriores) => [...anteriores, produtoSalvo])

            // Limpa os campos do formulário
            setNomeProduto('')
            setCodigoProduto('')
            setGtinProduto('')
            setPrecoProduto('')

            // Fecha o formulário
            setMostrarCadastro(false)

            alert('Produto cadastrado com sucesso!')
        } catch (erro) {
            console.error(erro)

            if (erro instanceof Error) {
                alert(erro.message)
            } else {
                alert('Ocorreu um erro inesperado.')
            }
        }
    }


    async function atualizarProduto() {
        // Verifica se existe um produto selecionado para edição.
        if (!produtoEmEdicao) return

        try {
            // Envia os dados atualizados para o backend Spring Boot.
            const resposta = await fetch(`/api/produtos/${produtoEmEdicao.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    nome: nomeEdicao,
                    codigoInterno: codigoEdicao,
                    gtin: gtinEdicao || null,
                    precoVenda: Number(precoEdicao),
                    ativo: produtoEmEdicao.ativo,
                }),
            })

            // Identifica o motivo da falha a partir do código HTTP.
            if (!resposta.ok) {

                // O backend rejeitou algum dado informado.
                if (resposta.status === 400) {
                    throw new Error('Dados inválidos. Verifique os campos do produto.')
                }

                // O código interno ou GTIN pode estar sendo utilizado.
                if (resposta.status === 409) {
                    throw new Error('Já existe outro produto com esse código ou GTIN.')
                }

                // O produto solicitado não foi encontrado.
                if (resposta.status === 404) {
                    throw new Error('Produto não encontrado.')
                }

                // Outros erros retornados pelo servidor.
                throw new Error('Não foi possível atualizar o produto.')
            }

            // Recebe do backend o produto com os dados atualizados.
            const produtoAtualizado: Produto = await resposta.json()

            // Substitui o produto antigo pelo atualizado na tabela.
            setProdutos((anteriores) =>
                anteriores.map((produto) =>
                    produto.id === produtoAtualizado.id
                        ? produtoAtualizado
                        : produto
                )
            )

            // Fecha o formulário de edição.
            setProdutoEmEdicao(null)

            alert('Produto atualizado com sucesso!')
        } catch (erro) {
            // Registra o erro no console para facilitar a investigação.
            console.error(erro)

            // Mostra ao operador a mensagem específica do erro.
            if (erro instanceof Error) {
                alert(erro.message)
            } else {
                alert('Ocorreu um erro inesperado ao atualizar o produto.')
            }
        }
    }


    return (
        <main>
            <header>
                <h1>Projeto PDV</h1>
                <p>Sistema de Ponto de Venda</p>
            </header>

            <nav>
                <button onClick={() => setTelaAtual('inicio')}>
                    Início
                </button>

                <button onClick={() => setTelaAtual('produtos')}>
                    Produtos
                </button>

                <button onClick={() => setTelaAtual('vendas')}>
                    Vendas
                </button>

                <button onClick={() => setTelaAtual('caixa')}>
                    Caixa
                </button>
            </nav>

            <section>
                {telaAtual === 'inicio' && (
                    <>
                        <h2>Bem-vindo ao Projeto PDV!</h2>
                        <p>Selecione uma opção para começar.</p>
                    </>
                )}

                {telaAtual === 'produtos' && (
                    <>
                        <h2>Consulta de Produtos</h2>


                        {produtoEmEdicao && (
                            <div className="formulario-produto">
                                <h3>Editar produto</h3>

                                <form
                                    onSubmit={(evento) => {
                                        // Impede que o navegador recarregue a página.
                                        evento.preventDefault()

                                        // Executa a atualização do produto.
                                        void atualizarProduto()
                                    }}
                                >
                                    <label htmlFor="nome-edicao">Nome do produto</label>
                                    <input
                                        id="nome-edicao"
                                        type="text"
                                        value={nomeEdicao}
                                        onChange={(evento) => setNomeEdicao(evento.target.value)}
                                        required
                                    />

                                    <label htmlFor="codigo-edicao">Código interno</label>
                                    <input
                                        id="codigo-edicao"
                                        type="text"
                                        value={codigoEdicao}
                                        onChange={(evento) => setCodigoEdicao(evento.target.value)}
                                    />

                                    <label htmlFor="gtin-edicao">GTIN / Código de barras</label>
                                    <input
                                        id="gtin-edicao"
                                        type="text"
                                        value={gtinEdicao}
                                        onChange={(evento) => setGtinEdicao(evento.target.value)}
                                    />

                                    <label htmlFor="preco-edicao">Preço de venda (R$)</label>
                                    <input
                                        id="preco-edicao"
                                        type="number"
                                        min="0.01"
                                        step="0.01"
                                        value={precoEdicao}
                                        onChange={(evento) => setPrecoEdicao(evento.target.value)}
                                        required
                                    />

                                    <button type="submit">Salvar alterações</button>

                                    <button
                                        type="button"
                                        onClick={() => setProdutoEmEdicao(null)}
                                    >
                                        Cancelar edição
                                    </button>
                                </form>
                            </div>
                        )}


                        <button
                            type="button"
                            onClick={() => setMostrarCadastro(!mostrarCadastro)}
                        >
                            {mostrarCadastro ? 'Cancelar cadastro' : '+ Novo produto'}
                        </button>


                        {mostrarCadastro && (
                            <div className="formulario-produto">
                                <h3>Cadastrar novo produto</h3>

                                <form onSubmit={(evento)=> {
                                    evento.preventDefault()
                                    void cadastrarProduto()
                                }}
                                >
                                    <label htmlFor="nome-produto">Nome do produto</label>
                                    <input
                                        id="nome-produto"
                                        type="text"
                                        placeholder="Ex.: Arroz 5kg"
                                        value={nomeProduto}
                                        onChange={(evento) => setNomeProduto(evento.target.value)}
                                        required
                                    />

                                    <label htmlFor="codigo-produto">Código interno</label>
                                    <input
                                        id="codigo-produto"
                                        type="text"
                                        placeholder="Ex.: ARR001"
                                        value={codigoProduto}
                                        onChange={(evento) => setCodigoProduto(evento.target.value)}
                                    />

                                    <label htmlFor="gtin-produto">GTIN / Código de barras</label>
                                    <input
                                        id="gtin-produto"
                                        type="text"
                                        placeholder="Digite o código de barras"
                                        value={gtinProduto}
                                        onChange={(evento) => setGtinProduto(evento.target.value)}
                                    />

                                    <label htmlFor="preco-produto">Preço de venda (R$)</label>
                                    <input
                                        id="preco-produto"
                                        type="number"
                                        min="0.01"
                                        step="0.01"
                                        placeholder="0,00"
                                        value={precoProduto}
                                        onChange={(evento) => setPrecoProduto(evento.target.value)}
                                        required
                                    />

                                    <button type="submit">
                                        Salvar produto
                                    </button>
                                </form>
                            </div>
                        )}


                        <input
                            type="text"
                            className="pesquisa-produto"
                            placeholder="Pesquisar produto pelo nome..."
                            value={pesquisa}
                            onChange={(evento) => setPesquisa(evento.target.value)}
                        />

                        {erro && <p>{erro}</p>}

                        <table>
                            <thead>
                            <tr>
                                <th>Código</th>
                                <th>Produto</th>
                                <th>Preço</th>
                                <th>Situação</th>
                                <th>Ações</th>
                            </tr>
                            </thead>

                            <tbody>
                            {produtosFiltrados.length === 0 && (
                                <tr>
                                    <td colSpan={5} className="sem-produtos">
                                        Nenhum produto encontrado.
                                    </td>
                                </tr>
                            )}

                            {produtosFiltrados.map((produto) => (
                                <tr key={produto.id}>
                                    <td>{produto.codigoInterno}</td>
                                    <td>{produto.nome}</td>
                                    <td>
                                        {produto.precoVenda.toLocaleString(
                                            'pt-BR',
                                            {
                                                style: 'currency',
                                                currency: 'BRL',
                                            }
                                        )}
                                    </td>
                                    <td>
                                        {produto.ativo ? 'Ativo' : 'Inativo'}
                                    </td>
                                    <td>
                                        <button
                                            type="button"
                                            onClick={() => {
                                                // Guarda o produto selecionado.
                                                setProdutoEmEdicao(produto)

                                                // Carrega os dados do produto no formulário de edição.
                                                setNomeEdicao(produto.nome)
                                                setCodigoEdicao(produto.codigoInterno)
                                                setGtinEdicao(produto.gtin ?? '')
                                                setPrecoEdicao(String(produto.precoVenda))
                                            }}
                                        >
                                            Editar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </>
                )}

                {telaAtual === 'vendas' && (
                    <>
                        <h2>Frente de Caixa</h2>
                        <p>Aqui vamos realizar as vendas.</p>
                    </>
                )}

                {telaAtual === 'caixa' && (
                    <>
                        <h2>Controle de Caixa</h2>
                        <p>Aqui vamos abrir, movimentar e fechar o caixa.</p>
                    </>
                )}
            </section>
        </main>
    )
}

export default App
