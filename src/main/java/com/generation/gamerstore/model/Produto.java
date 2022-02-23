package com.generation.gamerstore.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tb_produtos") 
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	@NotBlank(message = "O atributo nome é obrigatório!")
	@Size(min = 5, max = 100, message = "O atributo deve conter no mínimo 5 e no máximo 100 caract")
	private String nome;
	
	@NotBlank(message = "O atributo descricao é obrigatório!")
	@Size(min = 3, max = 1000, message = "O atributo texto deve conter no mínimo 5 e no máximo 1000 caract")
	private String descricao;
	
	@NotBlank(message = "O atributo quantidade é obrigatório!")
	@Size(min = 3, max = 1000, message = "O atributo texto deve conter no mínimo 5 e no máximo 1000 caract")
	private int quantidade;
	
	@NotBlank(message = "O atributo preço é obrigatório!")
	@Size(min = 3, max = 1000, message = "O atributo texto deve conter no mínimo 5 e no máximo 1000 caract")
	private BigDecimal preco;
	
		
	@ManyToOne								 //muitas produtos para UMA categoria
	@JsonIgnoreProperties("produto")		
	public Categoria categoria;
	

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	

}
