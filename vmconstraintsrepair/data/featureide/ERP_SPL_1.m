ERP_SPL : Cadastros Login [Listagens] Controledeestoque* Controlefinanceiro* :: _ERP_SPL ;

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

%%

not Controledeestoque or Listagemdeestoque ;
not Cadastrodeproduto or Listagemdeprodutos ;
not Cadastrodeservico or Listagemdeservicos ;
not Cadastrodefuncionario or Listagemdefuncionarios ;

