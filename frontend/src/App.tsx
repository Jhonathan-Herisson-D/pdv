
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

    // Armazena possíveis erros na consulta
    const [erro, setErro] = useState('')

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

                        {erro && <p>{erro}</p>}

                        <table>
                            <thead>
                            <tr>
                                <th>Código</th>
                                <th>Produto</th>
                                <th>Preço</th>
                                <th>Situação</th>
                            </tr>
                            </thead>

                            <tbody>
                            {produtos.map((produto) => (
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
