ERP_SPL : Cadastros Login [Listagens] Controledeestoque* Controlefinanceiro* Operacoes+ :: _ERP_SPL ;

Cadastros : Cadastrodecliente Cadastrodefornecedor [Cadastrodefuncionario] Cadastrodeusuario [Cadastroderegrasdeacessodeusuario] Cadastros_6+ :: _Cadastros ;

Cadastrodecliente : Editarcadastrodecliente Apagarcadastrodecliente :: _Cadastrodecliente ;

Cadastrodefornecedor : Editarcadastrodefornecedor Apagarcadastrodefornecedor :: _Cadastrodefornecedor ;

Cadastrodefuncionario : Editarcadastrodefuncionario Apagarcadastrodefuncionario :: _Cadastrodefuncionario ;

Cadastrodeusuario : Editarcadastrodeusuario Apagarcadastrodeusuario :: _Cadastrodeusuario ;

Cadastroderegrasdeacessodeusuario : Editarregrasdeacessodeusuario :: _Cadastroderegrasdeacessodeusuario ;

Cadastros_6 : [Cadastrodeproduto] [Cadastrodeservico] :: _Cadastros_6 ;

Cadastrodeproduto : Editarcadastrodeproduto Apagarcadastrodeproduto :: _Cadastrodeproduto ;

Cadastrodeservico : Editarcadastrodeservico Apagarcadastrodeservico :: _Cadastrodeservico ;

Login : [Controledeacesso] :: _Login ;

Listagens : Listagemdeitens [Listagemdeprodutos] [Listagemdeservicos] Listagemdepessoas Listagemdeclientes Listagemdefornecedores [Listagemdefuncionarios] Listagemdeusuarios [Listagemdeestoque] :: _Listagens ;

Controledeestoque : Entradadeestoque
	| Saidadeestoque
	| Manutencaodeestoque ;

Controlefinanceiro : Contasapagar
	| Contasareceber
	| Manutencaodelancamentos ;

Operacoes : Entradas* Saidas* :: _Operacoes ;

Entradas : [Ordensdecompra] [Entradasdenotafiscal] :: _Entradas ;

Ordensdecompra : [OC_Movimentarestoque] [OC_Gerarlancamentosapagar] :: _Ordensdecompra ;

Entradasdenotafiscal : ENF_Movimentarestoque ENF_Gerarlancamentosapagar :: _Entradasdenotafiscal ;

Saidas : [Orcamento] [Venda] :: _Saidas ;

Orcamento : [ORC_Movimentarestoque] [ORC_Gerarlancamentosareceber] :: _Orcamento ;

Venda : VEN_Movimentarestoque VEN_Gerarlancamentosareceber :: _Venda ;

%%

Controledeestoque or not Listagemdeestoque ;
Cadastrodeproduto or not Listagemdeprodutos ;
Cadastrodeservico or not Listagemdeservicos ;
Cadastrodefuncionario or not Listagemdefuncionarios ;

