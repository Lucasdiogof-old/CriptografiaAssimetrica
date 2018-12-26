import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

import javax.crypto.Cipher;

/*
* Conclusão: Na minha opinião a melhor implementação de criptografia é a RSA, pois nela eu tenho duas chaves, 
* uma pública e uma privada. A chave pública qualquer pessoa pode ter, pois ela só tem utilidade para criptografar uma mensagem, 
* mas a chave privada eu devo guardar em segredo, pois ela serve para decriptografar. Não é impossível quebrar a criptografia RSA,
* mas para fazer isto seriam necessários anos ou décadas, então a ideia se torna inviável.
 * Lucas Diogo França
 * 2016.2.0120.0023-5
 */

public class RSA {

	public static void main(String[] args) {

		try {

			Scanner ent = new Scanner(System.in);

			System.out.println("Digite o nome de um arquivo de texto:");
			String nomeArquivo = ent.next();

			FileReader arq = new FileReader(
					"C:\\Users\\Lucas-PC\\eclipse-workspace\\Criptografia Assimetrica\\src\\texto.txt");
			// Endereço do arquivo de texto que está no meu computador
			BufferedReader lerArq = new BufferedReader(arq);

			String linha = lerArq.readLine(); // Lê a primeira linha
			String txtArquivo = linha;

			while (linha != null) {
				linha = lerArq.readLine(); // lê da segunda até a última linha
				if (linha != null) {
					txtArquivo = txtArquivo + linha; // Lê o arquivo e monta a string que vai ser criptografada

				}
			}

			arq.close();

			// Verifica se já existem as chaves e se elas não existirem eu gero as chaves.
			if (!existeChave()) {

				// Gera as chaves
				gerarChave();

			}

			ObjectInputStream inputStream = null;

			// Criptografa a Mensagem usando a Chave Pública
			inputStream = new ObjectInputStream(new FileInputStream("C:/keys/public.key"));
			final PublicKey chavePublica = (PublicKey) inputStream.readObject();
			final byte[] textoCriptografado = criptografa(txtArquivo, chavePublica);

			// Decriptografa a Mensagem usando a Chave Pirvada
			inputStream = new ObjectInputStream(new FileInputStream("C:/keys/private.key"));
			final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
			final String textoPuro = decifrar(textoCriptografado, chavePrivada);

			// Imprime o texto original, o texto criptografado e
			// o texto descriptografado.
			System.out.println("Mensagem Original: " + txtArquivo);
			System.out.println("Mensagem Criptografada: " + textoCriptografado.toString());
			System.out.println("Mensagem Decriptografada: " + textoPuro);

			// TODO provavelmente no seu computador vai dar um erro professor, por não achar
			// esse diretório, então basta mudar pra um diretório aleatório nno seu
			// computador que o programa executa perfeitamente.

			File txtSaida = new File(
					"C:/Users/Lucas-PC/eclipse-workspace/Criptografia Assimetrica/src/" + nomeArquivo + ".txt -cif");

			txtSaida.createNewFile();

			// Salva a Chave Pública no arquivo
			ObjectOutputStream arquivoSaida = new ObjectOutputStream(new FileOutputStream(txtSaida));
			arquivoSaida.writeObject(textoCriptografado.toString());
			arquivoSaida.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void gerarChave() {
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			final KeyPair key = keyGen.generateKeyPair();

			File chavePrivadaFile = new File("C:/keys/private.key");
			File chavePublicaFile = new File("C:/keys/public.key");

			// Cria os arquivos para armazenar a chave Privada e a chave Publica
			if (chavePrivadaFile.getParentFile() != null) {
				chavePrivadaFile.getParentFile().mkdirs();
			}

			chavePrivadaFile.createNewFile();

			// Salva a Chave Privada no arquivo
			ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(new FileOutputStream(chavePrivadaFile));
			chavePrivadaOS.writeObject(key.getPrivate());
			chavePrivadaOS.close();

			if (chavePublicaFile.getParentFile() != null) {
				chavePublicaFile.getParentFile().mkdirs();
			}

			chavePublicaFile.createNewFile();

			// Salva a Chave Pública no arquivo
			ObjectOutputStream chavePublicaOS = new ObjectOutputStream(new FileOutputStream(chavePublicaFile));
			chavePublicaOS.writeObject(key.getPublic());
			chavePublicaOS.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Criptografa o texto puro usando chave pública.

	public static byte[] criptografa(String texto, PublicKey chavePublica) {
		byte[] cifraTexto = null;

		try {
			final Cipher cifra = Cipher.getInstance("RSA");
			// Criptografa o texto puro usando a chave Púlica
			cifra.init(Cipher.ENCRYPT_MODE, chavePublica);
			cifraTexto = cifra.doFinal(texto.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cifraTexto;
	}

	// Decriptografa o texto puro usando chave privada.

	public static String decifrar(byte[] texto, PrivateKey chavePrivada) {
		byte[] txtDecriptado = null;

		try {
			final Cipher cifra = Cipher.getInstance("RSA");
			// Decriptografa o texto puro usando a chave Privada
			cifra.init(Cipher.DECRYPT_MODE, chavePrivada);
			txtDecriptado = cifra.doFinal(texto);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(txtDecriptado);
	}

	// Verifica se as chaves já foram geradas, se sim, retorna true

	public static boolean existeChave() {

		File chavePrivada = new File("C:/keys/private.key");
		File chavePublica = new File("C:/keys/public.key");

		if (chavePrivada.exists() && chavePublica.exists()) {
			return true;
		}

		return false;
	}

}