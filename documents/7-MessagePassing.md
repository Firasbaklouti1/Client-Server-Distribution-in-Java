

Files et Passage de Messages
## Khaled Barbaria
khaled.barbaria@ensta.org
Faculté des Sciences de Bizerte
Cycle Ingénieur, 1ère année
## 2025-2026
Dernière modification : 26-11-2025 07 :37
Files et Passage de Messages1 / 21

## Logiciel
## Sûrcontreles
bugs
## Facileàcom-
prendre
Prêt  pour  le  chan-
gement
Correct   aujourd’hui
et  correct  dans  un
futur inconnu.
## Communication
claire avec les futurs
programmeurs,y
compris  votre  futur
vous.
Conçu  pour  s’adap-
ter  au  changement
sans réécriture.
Files et Passage de Messages2 / 21

Deux modèles pour la concurrence
- Mémoire partagée
Les modules concurrents
interagissent par lecture et écriture
d’objets mutables partagés en
mémoire
Exemple : Création de plusieurs
threads dans un seul processus Java
- Passage de messages
Les modules concurrents
interagissent en envoyant des
messages immuables via un canal de
communication
Exemple : Le pattern client/serveur
avec sockets réseau
Files et Passage de Messages3 / 21

Avantages du passage de messages
Plus grande sûreté contre les bugs
## 1
Interaction explicite : Les modules passent des messages via le canal de
communication, plutôt qu’implicitement par mutation de données partagées
## 2
Évite les interactions involontaires : L’interaction implicite de la mémoire partagée
peut trop facilement conduire à des interactions involontaires
## 3
Partage d’objets immuables : Le passage de messages partage uniquement des
objets immuables (les messages), tandis que la mémoire partagée nécessite le
partage d’objets mutables
Files et Passage de Messages4 / 21

Passage de messages avec des threads
Concept clé
Utiliser une file synchronisée pour le passage de messages entre threads
La file remplit la même fonction que le canal de communication réseau bufferisé dans le
passage de messages client/serveur.
Java fournit l’interface BlockingQueue pour les files avec opérations bloquantes
Cette conception est souvent préférable à une conception de mémoire partagée avec
verrous
Files et Passage de Messages5 / 21

Queue vs BlockingQueue
Queue ordinaire
add(e) : ajoute l’élément e à la fin de la file
remove() : retire et retourne l’élément en tête de file, ou lève une exception si la file
est vide
BlockingQueue
put(e) : bloque jusqu’à pouvoir ajouter l’élément e à la fin de la file
take() : bloque jusqu’à pouvoir retirer et retourner l’élément en tête de file,
attendant que la file soit non-vide
Important : Utiliser put() et take(), pas add() et remove() !
Files et Passage de Messages6 / 21

Pattern Producteur-Consommateur
## Producteur 1
## Producteur 2
File synchronisée
## Consommateur 1
## Consommateur 2
Les threads producteurs et consommateurs partagent une file synchronisée
Les producteurs mettent des données ou requêtes dans la file
Les consommateurs les retirent et les traitent
Files et Passage de Messages7 / 21

Implémentations de BlockingQueue en Java
ArrayBlockingQueue
File avec capacité fixe
put d’un nouvel élément bloquera si la file est pleine
LinkedBlockingQueue
File potentiellement non bornée
put ne bloquera jamais
Note importante : Les messages doivent être de type immuable pour éviter les
conditions de course.
Files et Passage de Messages8 / 21

Exemple : Compte bancaire
Chaque distributeur et chaque compte sont des modules independants
Les modules interagissent en s’envoyant des messages
Les messages entrants arrivent dans une file
Files et Passage de Messages9 / 21

Exemple bancaire : Problème de concurrence
Messages conçus : get-balance et withdraw
1  // Chaque distributeur verifie le solde avant retrait
2  get-balance
3  if balance >= 1 then withdraw 1
Problème : Il est toujours possible d’entrelacer les messages de deux distributeurs pour
qu’ils soient tous deux trompés en pensant pouvoir retirer en toute sécurité le dernier
dollar d’un compte avec seulement 1$ !
## Solution
Choisir une meilleure opération atomique :
withdraw-if-sufficient-funds
serait meilleure que simplement withdraw.
Files et Passage de Messages10 / 21

## Implémentation : Module Squarer (1/3)
## 1  /
## **
Effectue le carre d’entiers.
## *
## /
2  public class Squarer {
3      private final BlockingQueue<Integer> in;
4      private final BlockingQueue<SquareResult> out;
## 5
6      // Invariant de rep : in, out != null
## 7
## 8      /
## **
Cree un nouveau squarer.
## 9
## *
@param requests file pour recevoir les requetes
## 10
## *
@param replies file pour envoyer les reponses
## *
## /
11      public Squarer(BlockingQueue<Integer> requests,
12                     BlockingQueue<SquareResult> replies) {
13          this.in = requests;
14          this.out = replies;
## 15      }
Files et Passage de Messages11 / 21

## Implémentation : Module Squarer (2/3)
## 1      /
## **
Demarre le traitement des requetes de carre.
## *
## /
2      public void start() {
3          new Thread(new Runnable() {
4              public void run() {
5                  while (true) {
6                      // TODO: on peut vouloir un moyen d’arreter le thread
7                      try {
8                          // bloque jusqu’a l’arrivee d’une requete
9                          int x = in.take();
10                          // calcule la reponse et la renvoie
11                          int y = x
## *
x;
12                          out.put(new SquareResult(x, y));
13                      } catch (InterruptedException ie) {
14                          ie.printStackTrace();
## 15                      }
## 16                  }
## 17              }
## 18          }).start();
## 19      }
## 20  }
Files et Passage de Messages12 / 21

Implémentation : Classe SquareResult
## 1  /
## **
Un message de resultat de carre immuable.
## *
## /
2  public class SquareResult {
3      private final int input;
4      private final int output;
## 5
## 6      /
## **
Cree un nouveau message de resultat.
## 7
## *
@param input nombre d’entree
## 8
## *
@param output carre de l’entree
## *
## /
9      public SquareResult(int input, int output) {
10          this.input = input;
11          this.output = output;
## 12      }
## 13
14      @Override public String toString() {
15          return input + "^2 = " + output;
## 16      }
## 17  }
Note : On ajouterait probablement des observateurs pour récupérer l’entrée et la sortie.
Files et Passage de Messages13 / 21

Utilisation du Squarer
1  public static void main(String[] args) {
2      BlockingQueue<Integer> requests =
3          new LinkedBlockingQueue<>();
4      BlockingQueue<SquareResult> replies =
5          new LinkedBlockingQueue<>();
## 6
7      Squarer squarer = new Squarer(requests, replies);
8      squarer.start();
## 9
10      try {
11          // faire une requete
12          requests.put(42);
## 13
14          // ... peut-etre faire quelque chose concurremment ...
## 15
16          // lire la reponse
## 17          System.out.println(replies.take());
## 18
19      } catch (InterruptedException ie) {
20          ie.printStackTrace();
## 21      }
## 22  }
Files et Passage de Messages14 / 21

Arrêt du Squarer : Le problème
## Question
Que faire si nous voulons arrêter le Squarer pour qu’il n’attende plus de nouvelles
entrées ?
Dans le modèle client/serveur : on ferme le socket
Dans un processus séparé : on quitte le processus
Ici : Le squarer est juste un autre thread dans le même processus
Problème : On ne peut pas "fermer" une file
## Solutions :
## 1
Message spécial pour l’arrêt
## 2
Interruption de thread
Files et Passage de Messages15 / 21

Solution 1 : Message d’arrêt
## Concept
Un message spécial dans la file qui signale au consommateur de terminer son travail.
1  // Definir un nouveau type ADT pour les requetes :
2  SquareRequest = IntegerRequest + StopRequest
## 3
4  // Avec operations :
5  input : SquareRequest -> int
6  shouldStop : SquareRequest -> boolean
1  public void run() {
2      while (true) {
3          try {
4              SquareRequest req = in.take();
5              // verifier si on doit s’arreter
6              if (req.shouldStop()) { break; }
7              // calculer la reponse et la renvoyer
8              int x = req.input();
9              int y = x
## *
x;
10              out.put(new SquareResult(x, y));
11          } catch (InterruptedException ie) {
12              ie.printStackTrace();
## 13          }
## 14      }
## 15  }
Files et Passage de Messages16 / 21

Solution 2 : Interruption de thread
1  public void run() {
2      // traiter les requetes jusqu’a interruption
3      while (!Thread.interrupted()) {
4          try {
5              // bloque jusqu’a l’arrivee d’une requete
6              int x = in.take();
7              // calculer la reponse et la renvoyer
8              int y = x
## *
x;
9              out.put(new SquareResult(x, y));
10          } catch (InterruptedException ie) {
11              // arreter
12              break;
## 13          }
## 14      }
## 15  }
Appeler interrupt() sur le thread
Si le thread est bloqué : lève InterruptedException
Sinon : définit un flag interrupted
Files et Passage de Messages17 / 21

Arguments de sûreté des threads avec passage de messages
Un argument de sûreté des threads avec passage de messages peut s’appuyer sur :
## 1
Types de données threadsafe existants pour la file synchronisée
Cette file est définitivement partagée et définitivement mutable
## 2
Immuabilité des messages ou données accessibles à plusieurs threads en même
temps
## 3
Confinement des données aux threads producteur/consommateur individuels
Les variables locales d’un producteur/consommateur ne sont pas visibles des autres
threads
## 4
Confinement de messages mutables envoyés via la file mais accessibles à un seul
thread à la fois
Argument de "patate chaude" : un module abandonne toutes les références dès qu’il
met les données dans la file
Files et Passage de Messages18 / 21

Avantage du passage de messages
Facilite le maintien des invariants de sûreté des threads
En comparaison avec la synchronisation, le passage de messages peut faciliter le
maintien par chaque module de ses propres invariants de sûreté des threads.
Pas besoin de raisonner sur plusieurs threads accédant à des données partagées
Les données sont transférées entre modules via un canal de communication
threadsafe
Chaque module gère ses propres données de manière isolée
Files et Passage de Messages19 / 21

## Résumé
Points clés
Plutôt que de synchroniser avec des verrous, les systèmes de passage de messages
se synchronisent sur un canal de communication partagé (flux ou file)
Les threads communiquant avec des files bloquantes constituent un pattern utile
pour le passage de messages au sein d’un seul processus
Le passage de messages offre une plus grande sûreté contre les bugs comparé à
la mémoire partagée
Les messages doivent être immuables
Utiliser put() et take(), pas add() et remove()
Files et Passage de Messages20 / 21

Comparaison : Mémoire partagée vs Passage de messages
Mémoire partagéePassage de messages
Interaction impliciteInteraction explicite
Objets mutables partagésMessages immuables
Risque  d’interaction  involon-
taire
Communication claire via ca-
nal
Nécessite   verrous   et   syn-
chronisation
Synchronisation via file
Plus difficile à raisonnerPlus facile à maintenir
Files et Passage de Messages21 / 21