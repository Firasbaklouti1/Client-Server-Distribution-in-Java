

Programmation Concurrente et Distribuée
Rappels sur la programmation concurrente
## Khaled Barbaria
khaled.barbaria@ensta.org
Faculté des Sciences de Bizerte
Cycle Ingénieur, 1ère année
## 2025-2026
Dernière modification : 29-10-2025 08 :45
Rappels sur la programmation concurrente1 / 20

## Plan
## 1
## Introduction
## 2
Concepts Fondamentaux de la Concurrence
## 3
Threads en Java
Rappels sur la programmation concurrentePlan2 / 20

## Définitions Fondamentales I
## Programme
Ensemble d’instructions écrites dans un langage de programmation, stocké sur disque
(entité statique).
## Processus
Instance d’un programme en cours d’exécution, possédant :
Son propre espace d’adressage mémoire
Ses ressources système (fichiers, sockets, etc.)
Au moins un thread d’exécution
Rappels sur la programmation concurrenteIntroduction3 / 20

Définitions Fondamentales II
Thread (Fil d’exécution)
Unité d’exécution légère au sein d’un processus, partageant :
L’espace mémoire du processus parent
Les ressources du processus
Possède sa propre pile d’exécution et compteur de programme
Rappels sur la programmation concurrenteIntroduction4 / 20

Processus vs Threads
## Processus 1
## Mémoire
## T1T2
## Processus 2
## Mémoire
## T1T2
IsolésIsolés
Différences clés :
Processus : isolation mémoire, coût de création/changement de contexte élevé
Threads : mémoire partagée, changement de contexte rapide, mais risques de
concurrence
Rappels sur la programmation concurrenteIntroduction5 / 20

Intérêts de la Concurrence
## 1. Réactivité
Interface utilisateur non bloquante
Traitement asynchrone des événements
Exemple : UI thread + worker threads
## 2. Performance
Exploitation du parallélisme matériel
Utilisation optimale des CPU
multi-cœurs
Réduction du temps d’exécution global
## 3. Modularité
Séparation des préoccupations
Architecture plus claire
Maintenance facilitée
Exemple pratique
Serveur web : 1 thread = 1 requête client
Sans threads : traitement séquentiel
Avec threads : centaines de clients
simultanés
Rappels sur la programmation concurrenteIntroduction6 / 20

Modèle Mémoire et Partage des Ressources
## Mémoire Partagée
Tas (heap) : objets partagés
fichiers, sockets, etc
## Mémoire Privée
Pile (stack) : variables locales
Compteur de programme
Registres CPU
## Heap
(Partagé)
Stack T1Stack T2
## Conséquence Critique
Le partage de mémoire nécessite des mécanismes de synchronisation pour éviter les
conditions de course (race conditions).
Rappels sur la programmation concurrenteIntroduction7 / 20

Ordonnancement (Scheduling)
Définition : Décision du système d’exploitation sur quel thread exécuter à quel moment.
Types d’ordonnancement
## 1
Préemptif : L’OS peut interrompre un thread
## 2
Coopératif : Les threads cèdent volontairement le contrôle
Facteurs influençant l’ordonnancement :
Priorité des threads
État du thread (RUNNABLE, BLOCKED, WAITING)
Politique de l’OS (Round-Robin, priorité, temps réel)
Nombre de cœurs disponibles
## Important
L’ordre d’exécution des threads est non-déterministe sans synchronisation explicite. On
ne peut pas prédire l’interleaving exact des instructions.
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence8 / 20

Conditions de Course (Race Conditions)
Définition : Comportement incorrect causé par l’accès concurrent non synchronisé à des
données partagées.
1  class Counter {
2      private int count = 0;
## 3
4      public void increment() {
5          count++; // Non atomique !
## 6      }
## 7
8      public int getCount() {
9          return count;
## 10      }
## 11  }
1  // 2 threads executent
2  Counter c = new Counter();
## 3
4  Thread t1 = new Thread(() -> {
5      for(int i=0; i<1000; i++)
6          c.increment();
## 7  });
## 8
9  Thread t2 = new Thread(() -> {
10      for(int i=0; i<1000; i++)
11          c.increment();
## 12  });
13  // Resultat: count != 2000
Pourquoi ? L’opération count++ se décompose en 3 étapes :
## 1
Lire la valeur de count
## 2
Incrémenter la valeur
## 3
Écrire la nouvelle valeur
Ces étapes peuvent s’entrelacer (interleave) entre threads !
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence9 / 20

Interleaving et Atomicité
Interleaving : Entrelacements possibles des
opérations de plusieurs threads.
Exemple : count = 0 initialement
Thread 1Thread 2count
lire count (0)0
lire count (0)0
incrémenter (1)0
incrémenter (1)0
écrire (1)1
écrire (1)1
Résultat : Une incrémentation perdue !
## Atomicité
Une opération est atomique si elle s’exécute
comme une seule unité indivisible :
Soit elle se termine complètement
Soit elle ne commence pas
Pas d’état intermédiaire visible
## En Java :
Lecture/écriture variables : atomiques
pour types primitifs (sauf long,
double)
volatile : garantit l’atomicité même
pour long/double
Opérations composées : NON
atomiques
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence10 / 20

Blocage et Famine
Blocage (Blocking)
Un thread attend indéfiniment qu’une condition devienne vraie ou qu’une ressource soit
disponible.
## Causes :
Attente d’un verrou (lock)
Opérations I/O
Attente d’un signal (wait())
Famine (Starvation)
Un thread n’obtient jamais le CPU ou les ressources nécessaires pour progresser.
## Causes :
Priorités mal configurées
Threads de haute priorité monopolisant le CPU
Politique d’ordonnancement injuste
Solution : Utiliser des politiques d’ordonnancement équitables et gérer correctement les
priorités.
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence11 / 20

Interblocages (Deadlocks)
Définition : Situation où deux threads (ou plus) attendent indéfiniment des ressources
détenues mutuellement.
1  Object lock1 = new Object();
2  Object lock2 = new Object();
## 3
## 4  // Thread 1
5  synchronized(lock1) {
## 6      Thread.sleep(10);
7      synchronized(lock2) {
## 8          // Travail
## 9      }
## 10  }
## 11
## 12  // Thread 2
13  synchronized(lock2) {
## 14      Thread.sleep(10);
15      synchronized(lock1) {
## 16          // Travail
## 17      }
## 18  }
## T1T2
Lock1Lock2
possède
attend
possède
attend
Cycle de dépendances !
Conditions de Coffman (nécessaires pour un deadlock)
## 1
Exclusion mutuelle : ressources non partageables
## 2
Hold and wait : détenir et attendre d’autres ressources
## 3
Pas de préemption : impossible de forcer la libération
## 4
Attente circulaire : cycle de dépendances
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence12 / 20

Solutions aux Interblocages
- Prévention : Casser une condition de Coffman
Ordre global d’acquisition
Toujours acquérir les verrous dans le même ordre.
1  // Ordre par identite systeme
2  int id1 = System.identityHashCode(lock1);
3  int id2 = System.identityHashCode(lock2);
4  Object first = (id1 < id2) ? lock1 : lock2;
5  Object second = (id1 < id2) ? lock2 : lock1;
## 6
7  synchronized(first) {
8      synchronized(second) {
9          // Pas de deadlock possible
## 10      }
## 11  }
- Évitement : Détection et récupération
Utiliser tryLock() avec timeout (java.util.concurrent)
Implémenter un système de détection de cycles
Forcer l’abandon et la réessai
Rappels sur la programmation concurrenteConcepts Fondamentaux de la Concurrence13 / 20

Création de Threads : Méthode 1 - Hériter de Thread
1  class MonThread extends Thread {
2      @Override
3      public void run() {
4          System.out.println("Thread en execution: "
5                            + Thread.currentThread().getName());
6          // Travail du thread
## 7      }
## 8  }
## 9
## 10  // Utilisation
11  public class Main {
12      public static void main(String[] args) {
13          MonThread t1 = new MonThread();
14          MonThread t2 = new MonThread();
## 15
16          t1.start(); // Demarre le thread
17          t2.start();
## 18
19          // t1.run(); // ERREUR : execute dans le thread principal !
## 20      }
## 21  }
Limitation : Java n’autorise pas l’héritage multiple approche peu flexible.
Rappels sur la programmation concurrenteThreads en Java14 / 20

Création de Threads : Méthode 2 - Implémenter Runnable
1  class MaTache implements Runnable {
2      private String nom;
## 3
4      public MaTache(String nom) {
5          this.nom = nom;
## 6      }
## 7
8      @Override
9      public void run() {
10          System.out.println("Tache " + nom + " en execution");
## 11          // Travail...
## 12      }
## 13  }
## 14
## 15  // Utilisation
16  MaTache tache1 = new MaTache("A");
17  MaTache tache2 = new MaTache("B");
## 18
19  Thread t1 = new Thread(tache1);
20  Thread t2 = new Thread(tache2);
## 21
22  t1.start();
23  t2.start();
Avantage : Séparation entre tâche et thread, plus de flexibilité.
Rappels sur la programmation concurrenteThreads en Java15 / 20

Création de Threads : Méthode 3 - Lambda et Callable
Avec expression lambda (Java 8+) :
1  Thread t = new Thread(() -> {
2      System.out.println("Thread avec lambda");
## 3      // Travail...
## 4  });
5  t.start();
Avec Callable (retourne un résultat) :
1  import java.util.concurrent.
## *
## ;
## 2
3  Callable<Integer> tache = () -> {
4      // Calcul long...
5      return 42;
## 6  };
## 7
8  ExecutorService executor = Executors.newSingleThreadExecutor();
9  Future<Integer> resultat = executor.submit(tache);
## 10
11  Integer valeur = resultat.get(); // Bloque jusqu’au resultat
12  System.out.println("Resultat: " + valeur);
## 13
14  executor.shutdown();
Callable permet de retourner une valeur et de lever des exceptions.
Rappels sur la programmation concurrenteThreads en Java16 / 20

Cycle de Vie d’un Thread
## NEW
## RUNNABLE
## BLOCKED
## WAITING/
## TIMED_WAITING
## TERMINATED
start()
attend lock
obtient lock
wait()/join()notify()/timeout
fin run()
NEW : Thread créé mais pas démarré
RUNNABLE : Thread exécutable (en cours ou prêt)
BLOCKED : En attente d’un verrou de moniteur
WAITING : En attente indéfinie d’un autre thread
TIMED_WAITING : En attente avec timeout
TERMINATED : Thread terminé
Rappels sur la programmation concurrenteThreads en Java17 / 20

Méthodes Importantes de Thread
## 1  // Demarrage
2  thread.start();              // Demarre l’execution
## 3
## 4  // Information
5  Thread.currentThread();      // Obtient le thread actuel
6  thread.getName();            // Nom du thread
7  thread.getId();              // ID unique
8  thread.getState();           // Etat actuel (NEW, RUNNABLE, ...)
9  thread.isAlive();            // true si actif
## 10
## 11  // Synchronisation
12  thread.join();               // Attend la fin du thread
13  thread.join(1000);           // Attend max 1 seconde
## 14
## 15  // Interruption
16  thread.interrupt();          // Envoie un signal d’interruption
17  Thread.interrupted();        // Verifie et efface le flag d’interruption
18  thread.isInterrupted();      // Verifie sans effacer
## 19
20  // Attente (   utiliser avec pr caution)
21  Thread.sleep(1000);          // Dort 1 seconde
22  Thread.yield();              // Suggere de ceder le CPU
Attention : stop(), suspend(), resume() sont dépréciées (dangereuses).
Rappels sur la programmation concurrenteThreads en Java18 / 20

Gestion de l’Interruption
Pattern correct pour arrêter un thread :
1  class TacheInterruptible implements Runnable {
2      @Override
3      public void run() {
4          try {
5              while (!Thread.currentThread().isInterrupted()) {
## 6                  // Travail...
7                  System.out.println("Travail en cours...");
8                  Thread.sleep(1000); // Point d’interruption
## 9              }
10          } catch (InterruptedException e) {
11              // Thread interrompu pendant sleep
12              System.out.println("Thread interrompu");
13              // Retablir le flag d’interruption
14              Thread.currentThread().interrupt();
15          } finally {
16              // Nettoyage des ressources
17              System.out.println("Nettoyage...");
## 18          }
## 19      }
## 20  }
## 21
## 22  // Utilisation
23  Thread t = new Thread(new TacheInterruptible());
24  t.start();
## 25  Thread.sleep(5000);
26  t.interrupt(); // Demande arret propre
27  t.join();      // Attend la fin
Rappels sur la programmation concurrenteThreads en Java19 / 20

Priorités et Scheduling en Java
Priorités des threads :
Plage : Thread.MIN_PRIORITY (1) à Thread.MAX_PRIORITY (10)
Par défaut : Thread.NORM_PRIORITY (5)
Héritage : un thread hérite de la priorité de son créateur
Limitations importantes
Les priorités sont des suggestions à la JVM, pas des garanties
Comportement dépendant de l’OS et de la JVM
Peut causer de la famine si mal utilisé
Ne pas utiliser comme mécanisme principal de synchronisation
Thread daemon :
thread.setDaemon(true) : thread de service en arrière-plan
JVM se termine quand il ne reste que des threads daemon
Utile pour : garbage collection, monitoring, etc.
Doit être appelé avant start()
Rappels sur la programmation concurrenteThreads en Java20 / 20