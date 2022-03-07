package com.generation.gamerstore.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.gamerstore.model.Usuario;
import com.generation.gamerstore.model.UsuarioLogin;
import com.generation.gamerstore.repository.UsuarioRepository;



@Service
public class UsuarioService {

	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	/**
	 *  Cadastrar Usuário
	 * 
	 *  Checa se o usuário já existe no Banco de Dados através do método findByUsuario, 
	 *  porquê não pode existir 2 usuários com o mesmo email. 
	 *  Se não existir retorna um Optional vazio.
	 *  
	 *  isPresent() -> Se um valor estiver presente retorna true, caso contrário
	 *  retorna false.
	 * 
	 *  empty -> Retorna uma instância de Optional vazia.
	 */
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
		/**
		 * Se o Usuário não existir no Banco de Dados, a senha será criptografada
		 * através do Método criptografarSenha.
		 */
		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		/**
		 * Assim como na Expressão Lambda, o resultado do método save será retornado dentro
		 * de um Optional, com o Usuario persistido no Banco de Dados.
		 * 
		 * of​ -> Retorna um Optional com o valor fornecido, mas o valor não pode ser nulo. 
		 * Se não tiver certeza de que o valor não é nulo use ofNullable.
		 */
		return Optional.of(usuarioRepository.save(usuario));
	
	}

	/**
	 *  Atualizar Usuário
	 * 
	 *  Checa se o usuário já existe no Banco de Dados através do método findById, 
	 *  porquê não é possíve atualizar 1 usuário inexistente. 
	 *  Se não existir retorna um Optional vazio.
	 *  
	 *  isPresent() -> Se um valor estiver presente retorna true, caso contrário
	 *  retorna false.
	 * 
	 */
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {
			
			/**
		 	* Se o Usuário existir no Banco de Dados, a senha será criptografada
		 	* através do Método criptografarSenha.
		 	*/
			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			/**
		 	* Assim como na Expressão Lambda, o resultado do método save será retornado dentro
		 	* de um Optional, com o Usuario persistido no Banco de Dados ou um Optional vazio,
			* caso aconteça algum erro.
			* 
			* ofNullable​ -> Se um valor estiver presente, retorna um Optional com o valor, 
			* caso contrário, retorna um Optional vazio.
		 	*/
			return Optional.ofNullable(usuarioRepository.save(usuario));
			
		}
		
		/**
		 * empty -> Retorna uma instância de Optional vazia, caso o usuário não seja encontrado.
		 */
		return Optional.empty();
	
	}	

	/**
	 *  A principal função do método autenticarUsuario, que é executado no endpoint logar,
	 *  é gerar o token do usuário codificado em Base64. O login prorpiamente dito é executado
	 *  pela BasicSecurityConfig em conjunto com as classes UserDetailsService e Userdetails
	 */
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		/**
		 * Cria um objeto Optional do tipo Usuario para receber o resultado do 
		 * método findByUsuario().
		 * 
		 * Observe que o método autenticarUsuario recebe como parâmetro um objeto da
		 * Classe UsuarioLogin, ao invés de Usuario.
		 * 
		 * get() -> Se um valor estiver presente no objeto ele retorna o valor, caso contrário,
		 * lança uma Exception NoSuchElementException. Então para usar get é preciso ter certeza 
		 * de que o Optional não está vazio.
		 * O get() funciona como uma chave que abre o Objeto Optional e permite acessar os Métodos
		 * do Objeto encpsulado.
		 * 
		 */
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

		/**
		 * Checa se o usuario existe
		 */
		if (usuario.isPresent()) {

			/**
			 *  Checa se a senha enviada, depois de criptografada, é igual a senha
			 *  gravada no Banco de Dados, através do Método compararSenhas.
			 * 
			 *  O Método Retorna verdadeiro se as senhas forem iguais, e falso caso contrário.
			 * 
			 */
			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {

				/**
				 * Se as senhas forem iguais, atualiza o objeto usuarioLogin com os dados 
				 * recuperados do Banco de Dados e insere o Token Gerado através do Método
				 * gerarBasicToken.
				 */
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());

				/**
				 * Retorna o objeto usarioLogin atualizado para a classe UsuarioController.
				 * A Classe controladora checará se deu tudo certo nesta operação e retornará
				 * o status.
				 */
				return usuarioLogin;

			}
		}	
		
		/**
		 * empty -> Retorna uma instância de Optional vazia, caso o usuário não seja encontrado.
		 */
		return Optional.empty();
		
	}

	/**
	*  Método Criptografar Senhas.
	*   
	*  Instancia um objeto da Classe BCryptPasswordEncoder para criptografar
	*  a senha do usuário.
	*
	*  O método encode retorna a senha criptografada no formato BCrypt. Para mais detalhes,
	*  consulte a documentação do BCryptPasswordEncoder.
	* 
	*/
	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);

	}
	
	
	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.matches(senhaDigitada, senhaBanco);

	}

	private String gerarBasicToken(String usuario, String senha) {

		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);

	}
}
