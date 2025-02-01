package CyrilEnzoI.TP1_ASI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class Tp1AsiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Tp1AsiApplication.class, args);

		Scanner scanner = new Scanner(System.in);
		boolean continuer = true;

		while (continuer) {
			// Demande à l'utilisateur quel exercice lancer
			System.out.println("\nChoisissez l'exercice à exécuter :\n1. JMS Queue\n2. JMS Topic\n==>");
			int choix = scanner.nextInt();

			switch (choix) {
				case 1:
					System.out.println("Lancement de l'exercice 1 : JMS Queue");
					try {
						JmsQueueExample.main(args);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					System.out.println("Lancement de l'exercice 2 : JMS Topic");
					try {
						JmsTopicExample.main(args);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("Choix invalide. Veuillez entrer 1 ou 2.");
					continue;
			}

			// Demande si l'utilisateur veut exécuter un autre exercice
			System.out.println("\nVoulez-vous exécuter un autre exercice ? (oui/non)\n==>");
			String reponse = scanner.next();
			continuer = reponse.equalsIgnoreCase("oui");

			if (!continuer) {
				System.out.println("Merci d'avoir utilisé le programme. Au revoir !");
			}
		}

		scanner.close();
	}
}
