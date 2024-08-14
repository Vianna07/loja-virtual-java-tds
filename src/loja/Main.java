package loja;

public class Main {	
	public static void main(String[] args) {
		Endereco endereco = new Endereco("Rua Catapimbas", "4232", "Bairro para demolir", "Cidade de deus", "Estado corrupto", "67125-009");
		Usuario usuario = new Usuario(1, "Vianna", "vianna@gmailcom", "12345678", endereco);
		
		System.out.println(usuario.toString());
	}
}
