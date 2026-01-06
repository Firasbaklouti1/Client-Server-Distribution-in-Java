

Programmation Concurrente et Distribuée
Synchronisation en Java, Verrous et Variables conditionnelles
## Khaled Barbaria
khaled.barbaria@ensta.org
Faculté des Sciences de Bizerte
Cycle Ingénieur, 1ère année
## 2025-2026
Dernière modification : 12-11-2025 10 :38
Synchronisation en Java, Verrous et Variables conditionnelles1 / 46

Plan de la séance
## 1
Introduction à la Synchronisation
## 2
Mécanismes de Synchronisation
## 3
Verrous explicites (Lock)
## 4
Variables conditionnelles
## 5
Problèmes de Visibilité
## 6
## Bonnes Pratiques
Synchronisation en Java, Verrous et Variables conditionnelles2 / 46

Objectifs pédagogiques
Compétences visées
Comprendre la nécessité de la synchronisation en environnement multithreadé
Identifier les problèmes d’accès concurrent aux ressources partagées
Maîtriser les mécanismes de synchronisation en Java
Choisir l’outil approprié selon le contexte
## Prérequis
Connaissance des threads Java, notion de concurrence
Synchronisation en Java, Verrous et Variables conditionnellesIntroduction à la Synchronisation3 / 46

Contexte : Exécution concurrente
Threads et ressources partagées
Plusieurs threads accèdent aux mêmes
données
Mémoire partagée = source de conflits
Ordonnancement non déterministe
## Mémoire
## T1T2
## Conflit !
Synchronisation en Java, Verrous et Variables conditionnellesIntroduction à la Synchronisation4 / 46

## Problème 1 : Race Condition
1  public class Counter {
2      private int count = 0;
## 3
4      public void increment() {
5          count++;  // Non atomique !
## 6      }
## 7
8      public int getCount() {
9          return count;
## 10      }
## 11  }
Décomposition de count++
## 1
Lire count
## 2
## Incrémenter
## 3
Écrire count
## Résultat
Avec 2 threads × 1000 itérations :
## Attendu : 2000
## Obtenu : 1342, 1876, ...
Synchronisation en Java, Verrous et Variables conditionnellesIntroduction à la Synchronisation5 / 46

## Problème 2 : Inconsistency
Scénario : Compte bancaire
Thread 1 : Vérifier solde≥ 100C
Thread 2 : Retirer 50C
Thread 1 : Retirer 100C
Résultat : Solde négatif ! (découvert non autorisé)
État incohérent
L’objet passe par des états intermédiaires invalides visibles par d’autres threads
Synchronisation en Java, Verrous et Variables conditionnellesIntroduction à la Synchronisation6 / 46

Problème 3 : Problèmes de visibilité
Modèle mémoire Java
Chaque thread possède un cache local
Les écritures peuvent ne pas être
visibles immédiatement
Réordonnancement des instructions par
le compilateur/OS/CPU
## Conséquence
Un thread peut lire une valeur obsolète d’une variable
Synchronisation en Java, Verrous et Variables conditionnellesIntroduction à la Synchronisation7 / 46

Le mot-clé synchronized
## Principe
Garantit l’exclusion mutuelle sur un moniteur (objet verrou)
Méthode synchronisée
1  public synchronized
2      void increment() {
3      count++;
## 4  }
Verrou : this
Bloc synchronisé
1  public void increment() {
2      synchronized(this) {
3          count++;
## 4      }
## 5  }
Verrou explicite
## Garanties
+ Exclusion mutuelle+ Visibilité mémoire (happens-before)
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation8 / 46

Exemple : Compteur synchronisé
1  public class SynchronizedCounter {
2      private int count = 0;
## 3
4      public synchronized void increment() {
5          count++;  // Section critique protégée
## 6      }
## 7
8      public synchronized int getCount() {
9          return count;  // Lecture cohérente
## 10      }
## 11  }
## 12
## 13  // Utilisation
14  SynchronizedCounter counter = new SynchronizedCounter();
15  Thread t1 = new Thread(() -> {
16      for (int i = 0; i < 1000; i++) counter.increment();
## 17  });
18  Thread t2 = new Thread(() -> {
19      for (int i = 0; i < 1000; i++) counter.increment();
## 20  });
21  t1.start(); t2.start();
22  t1.join(); t2.join();
23  System.out.println(counter.getCount());  // Toujours 2000 !
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation9 / 46

Réentrance du verrou synchronized en Java I
Définition : Le verrou associé au mot-clé synchronized est réentrant. Cela signifie
qu’un même thread peut acquérir plusieurs fois le même verrou sans se bloquer
lui-même.
## Principe
Lorsqu’un thread détient déjà le verrou d’un objet, il peut entrer à nouveau dans un
bloc ou une méthode synchronized sur le même objet.
Chaque verrou garde un compteur de réentrance :
+1 à chaque acquisition par le même thread.
–1 à chaque sortie du bloc.
Libération du verrou seulement lorsque le compteur atteint 0.
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation10 / 46

Réentrance du verrou synchronized en Java II
## Exemple
1  class Exemple {
2      public synchronized void methodeA() {
3          System.out.println("methodeA");
4          methodeB(); // Appel d’une autre méthode synchronisée
## 5      }
## 6
7      public synchronized void methodeB() {
8          System.out.println("methodeB");
## 9      }
## 10  }
Remarque : Le verrou ReentrantLock de java.util.concurrent.locks est
également réentrant, mais géré explicitement.
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation11 / 46

## Moniteur Java I
En Java, chaque objet possède un moniteur,
Un moniteur Java comprend entre autres :
## 1
Un verrou
## 2
La file d’entrée (Entry Set / Lock Queue)
## 3
La file d’attente (Wait Set / Condition Queue)
La file d’entrée (Entry Set / Lock Queue)
Contient les threads qui attendent d’acquérir le verrou
Quand un thread essaie d’entrer dans un bloc synchronized déjà occupé, il est placé
dans cette file
Dès que le verrou se libère, un thread de cette file l’acquiert
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation12 / 46

Moniteur Java II
Mécanisme de verrouillage
Un seul thread possède le verrou à la fois
Les autres threads sont mis en attente (BLOCKED)
Libération automatique à la sortie du bloc/méthode
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation13 / 46

Coût de la synchronisation
## Performance
Overhead : acquisition/libération du verrou
Contention : threads en attente = CPU inutilisé
Problèmes d’échelle (scalability)
Bonnes pratiques
Minimiser la taille des sections critiques
Éviter les opérations lentes dans synchronized
Préférer des structures lock-free si possible
Synchronisation en Java, Verrous et Variables conditionnellesMécanismes de Synchronisation14 / 46

Interface java.util.concurrent.locks.Lock I
API principale
1  public interface Lock {
2      void lock();          // Acquérir (bloquant)
3      void unlock();        // Libérer
4      boolean tryLock();    // Tentative non bloquante
5      boolean tryLock(long time, TimeUnit unit); // Avec timeout
6      Condition newCondition(); // Pour coordination
## 7  }
Implémentation standard
ReentrantLock : Verrou réentrant (un thread peut l’acquérir plusieurs fois)
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)15 / 46

Utilisation de ReentrantLock
1  import java.util.concurrent.locks.
## *
## ;
## 2
3  public class LockCounter {
4      private final Lock lock = new ReentrantLock();
5      private int count = 0;
## 6
7      public void increment() {
8          lock.lock();  // Acquérir le verrou
9          try {
10              count++;  // Section critique
11          } finally {
12              lock.unlock();  // Toujours libérer dans finally !
## 13          }
## 14      }
## 15
16      public int getCount() {
17          lock.lock();
18          try {
19              return count;
20          } finally {
21              lock.unlock();
## 22          }
## 23      }
## 24  }
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)16 / 46

Méthode tryLock() : éviter le blocage
1  public class BankAccount {
2      private final Lock lock = new ReentrantLock();
3      private double balance;
## 4
5      public boolean withdraw(double amount) {
6          if (lock.tryLock()) {  // Tentative sans attente
7              try {
8                  if (balance >= amount) {
9                      balance -= amount;
10                      return true;
## 11                  }
12                  return false;
13              } finally {
14                  lock.unlock();
## 15              }
16          } else {
17              System.out.println("Compte occupé, réessayez plus tard");
18              return false;  // Opération échouée (non bloqué)
## 19          }
## 20      }
## 21  }
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)17 / 46

tryLock() avec timeout
1  import java.util.concurrent.TimeUnit;
## 2
3  public void transferMoney(BankAccount from, BankAccount to,
4                            double amount) throws InterruptedException {
5      // Tentative d’acquérir les deux verrous avec timeout
6      if (from.lock.tryLock(1, TimeUnit.SECONDS)) {
7          try {
8              if (to.lock.tryLock(1, TimeUnit.SECONDS)) {
9                  try {
10                      from.balance -= amount;
11                      to.balance += amount;
12                  } finally {
13                      to.lock.unlock();
## 14                  }
## 15              }
16          } finally {
17              from.lock.unlock();
## 18          }
19      } else {
20          throw new TimeoutException("Impossible d’acquérir les verrous");
## 21      }
## 22  }
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)18 / 46

Comparaison : synchronized vs Lock
CaractéristiquesynchronizedLock
## Simplicité+++
Libération automatique+- (finally requis)
Tentative non bloquante-+ (tryLock)
## Timeout-+
Interruptibilité-+ (lockInterruptibly)
Conditions multiples-+ (Condition)
Équité (fairness)-+ (ReentrantLock)
Ordre d’acquisitionNon contrôlableParamétrable
PerformanceOptimisé JVMLéger overhead
## Recommandation
Utiliser synchronized par défaut. Passer à Lock si besoin de flexibilité avancée.
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)19 / 46

ReentrantLock : mode équitable (fair)
1  // Mode non équitable (par défaut) : plus rapide, mais pas d’ordre
2  Lock unfairLock = new ReentrantLock();
## 3
4  // Mode équitable : respect de l’ordre d’arrivée (FIFO)
5  Lock fairLock = new ReentrantLock(true);
Fair vs Unfair
Unfair : Threads récemment arrivés peuvent passer devant
+  Meilleur débit (throughput)
−  Risque de starvation
Fair : Garantit l’ordre d’acquisition
+  Pas de starvation
−  Performance réduite
Synchronisation en Java, Verrous et Variables conditionnellesVerrous explicites (Lock)20 / 46

Variables conditionnelles I
Dans un programme concurrent, il arrive qu’un thread doive attendre une condition
avant de continuer.
Exemple typique : un producteur ne doit pas déposer dans un tampon plein, un
consommateur ne doit pas lire un tampon vide.
Ces situations nécessitent un mécanisme d’attente et de réveil : les variables
conditionnelles.
Producteur-Consommateur : Problème de l’attente active
1  class Tampon {
2      private int data;
3      private boolean plein = false;
## 4
5      public synchronized void produire(int x) {
6          while (plein) {
7              // Attente active : gaspillage CPU
## 8          }
9          data = x;
10          plein = true;
## 11      }
## 12
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles21 / 46

Variables conditionnelles II
13      public synchronized int consommer() {
14          while (!plein) {
15              // Attente active : inefficace
## 16          }
17          plein = false;
18          return data;
## 19      }
## 20  }
L’attente active (busy waiting) consomme inutilement du CPU.
Les Variables Conditionnelles remplacent l’attente active par une attente passive
efficace : wait() / notify().
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles22 / 46

Mécanisme des variables conditionnelles en Java
Chaque objet Java possède implicitement une file d’attente de condition.
Les méthodes wait(), notify(), notifyAll() permettent :
à un thread d’attendre une condition sans occuper le CPU ;
à un autre thread de réveiller les threads en attente.
Ces opérations ne sont possibles que dans un bloc synchronized.
La file d’attente (Wait Set / Condition Queue)
Contient les threads qui ont appelé wait() sur l’objet
Ces threads ont volontairement libéré le verrou et attendent une notification
Ils sont réveillés par notify() ou notifyAll()
Après réveil, ils doivent réacquérir le verrou (retournent dans la file d’entrée)
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles23 / 46

Cycle d’attente et de notification
Thread détient le verrou
Thread appelle wait() et libère le verrou
Thread en attente dans la file de condition
notify() ou notifyAll()
Thread reprend le verrou et continue
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles24 / 46

Implémentation correcte : Producteur-Consommateur I
1  class Tampon {
2      private int data;
3      private boolean plein = false;
## 4
5      public synchronized void produire(int x) throws InterruptedException {
6          while (plein) {
7              wait(); // attend que le tampon soit vide
## 8          }
9          data = x;
10          plein = true;
11          notifyAll(); // réveille les consommateurs
## 12      }
## 13
14      public synchronized int consommer() throws InterruptedException {
15          while (!plein) {
16              wait(); // attend que le tampon soit plein
## 17          }
18          plein = false;
19          notifyAll(); // réveille les producteurs
20          return data;
## 21      }
## 22  }
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles25 / 46

Implémentation correcte : Producteur-Consommateur II
## Mécanisme
wait() libère le verrou associé à l’objet et place le thread dans la file de condition.
Lorsqu’un autre thread appelle notify() :
un thread en attente est déplacé vers la file des threads prêts ;
il devra réacquérir le verrou avant de continuer.
notifyAll() réveille tous les threads en attente.
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles26 / 46

Buffer à taille limitée
1  public class BoundedBuffer<T> {
2      private final Queue<T> queue = new LinkedList<>();
3      private final int capacity;
## 4
5      public synchronized void put(T item) throws InterruptedException {
6          while (queue.size() == capacity) {
7              wait();  // File pleine, attendre
## 8          }
9          queue.add(item);
10          notifyAll();  // Réveiller les consommateurs
## 11      }
## 12
13      public synchronized T take() throws InterruptedException {
14          while (queue.isEmpty()) {
15              wait();  // File vide, attendre
## 16          }
17          T item = queue.remove();
18          notifyAll();  // Réveiller les producteurs
19          return item;
## 20      }
## 21  }
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles27 / 46

Erreurs fréquentes avec wait/notify
Erreur 1 : Oublier synchronized
wait() et notify() doivent être appelés dans un bloc synchronized
Erreur 2 : Utiliser if au lieu de while
Spurious wakeups : réveils inattendus possibles
Autre thread peut modifier la condition avant le réveil
Toujours revérifier la condition après wait()
Erreur 3 : notify() au lieu de notifyAll()
notify() réveille un seul thread risque d’interblocage si plusieurs types de threads
attendent
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles28 / 46

Conditions avec ReentrantLock
1  public class BoundedBufferWithLock<T> {
2      private final Lock lock = new ReentrantLock();
3      private final Condition notFull = lock.newCondition();
4      private final Condition notEmpty = lock.newCondition();
5      private final Queue<T> queue = new LinkedList<>();
## 6
7      public void put(T item) throws InterruptedException {
8          lock.lock();
9          try {
10              while (queue.size() == capacity) {
11                  notFull.await();  // Équivalent de wait()
## 12              }
13              queue.add(item);
14              notEmpty.signal();  // Équivalent de notify()
15          } finally {
16              lock.unlock();
## 17          }
## 18      }
19      // take() similaire avec notEmpty.await() et notFull.signal()
## 20  }
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles29 / 46

Classes utilitaires modernes
java.util.concurrent
Des abstractions de plus haut niveau pour éviter wait/notify
CountDownLatch  Attendre que N événements se produisent
CyclicBarrier  Synchroniser N threads à un point de rendez-vous
Semaphore  Limiter l’accès à N ressources simultanément
Phaser  Barrière flexible avec phases multiples
Exchanger  Échanger des données entre deux threads
## Recommandation
Préférer ces classes à wait/notify quand applicable
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles30 / 46

Exemple : CountDownLatch
1  import java.util.concurrent.CountDownLatch;
## 2
3  public class WorkerCoordination {
4      public static void main(String[] args) throws InterruptedException {
5          int numWorkers = 3;
6          CountDownLatch latch = new CountDownLatch(numWorkers);
## 7
8          for (int i = 0; i < numWorkers; i++) {
9              new Thread(() -> {
10                  System.out.println("Worker terminé");
11                  latch.countDown();  // Décrémenter le compteur
## 12              }).start();
## 13          }
## 14
15          latch.await();  // Attendre que tous les workers terminent
16          System.out.println("Tous les workers ont terminé !");
## 17      }
## 18  }
Synchronisation en Java, Verrous et Variables conditionnellesVariables conditionnelles31 / 46

Rappel : Modèle mémoire Java (JMM)
## Problèmes
Caches : Chaque cœur CPU a son cache L1/L2
Réordonnancement : Compilateur et CPU peuvent réorganiser les instructions
Registres : Variables peuvent rester en registre
## Conséquence
Sans synchronisation, un thread peut ne jamais voir les modifications d’un autre thread !
Relation happens-before
Le JMM définit quand les modifications d’un thread sont visibles par un autre
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité32 / 46

Mot-clé volatile
## Garanties
Visibilité : Lecture/écriture directe en mémoire principale
Ordre : Pas de réordonnancement autour des accès volatile
Atomicité : - Lecture/écriture atomique, mais pas opérations composées
1  public class StoppableTask implements Runnable {
2      private volatile boolean running = true;
## 3
4      public void run() {
5          while (running) {  // Voit toujours la dernière valeur
## 6              // Traitement...
## 7          }
## 8      }
## 9
10      public void stop() {
11          running = false;  // Visible immédiatement par le thread
## 12      }
## 13  }
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité33 / 46

Volatile : ce que ça NE fait PAS
Attention : volatile̸= atomique
1  private volatile int counter = 0;
## 2
3  public void increment() {
4      counter++;  // DANGEREUX : lecture + écriture non atomiques !
## 5  }
Quand utiliser volatile ?
Drapeaux booléens (flags)
Variables de statut
Pattern de double-checked locking
Jamais pour des opérations composées (++, +=, etc.)
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité34 / 46

Classes atomiques
java.util.concurrent.atomic
Opérations atomiques sans verrous (lock-free, CAS : Compare-And-Swap)
1  import java.util.concurrent.atomic.
## *
## ;
## 2
3  public class AtomicCounter {
4      private AtomicInteger count = new AtomicInteger(0);
## 5
6      public void increment() {
7          count.incrementAndGet();  // Atomique !
## 8      }
## 9
10      public int getCount() {
11          return count.get();
## 12      }
## 13
14      public boolean compareAndSet(int expected, int newValue) {
15          return count.compareAndSet(expected, newValue);
## 16      }
## 17  }
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité35 / 46

Classes atomiques disponibles
Types primitifs
AtomicBoolean
AtomicInteger
AtomicLong
## Références
AtomicReference<V>
AtomicStampedReference<V>
## Tableaux
AtomicIntegerArray
AtomicLongArray
AtomicReferenceArray<E>
Mise à jour de champs
AtomicIntegerFieldUpdater
AtomicLongFieldUpdater
AtomicReferenceFieldUpdater
## Performance
Souvent plus rapides que synchronized pour des opérations simples
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité36 / 46

Compare-And-Swap (CAS)
## Principe
Opération atomique implémentée au niveau matériel (CPU)
1  // Pseudo-code de compareAndSet
2  boolean compareAndSet(int expected, int newValue) {
3      if (currentValue == expected) {
4          currentValue = newValue;
5          return true;
## 6      }
7      return false;
## 8  }
9  // Le tout de manière ATOMIQUE !
Exemple d’utilisation
1  AtomicInteger counter = new AtomicInteger(0);
2  int oldValue, newValue;
3  do {
4      oldValue = counter.get();
5      newValue = oldValue + 1;
6  } while (!counter.compareAndSet(oldValue, newValue));
Synchronisation en Java, Verrous et Variables conditionnellesProblèmes de Visibilité37 / 46

Principes généraux
- Minimiser les sections critiques
Synchroniser uniquement le strict nécessaire
Faire les calculs en dehors de synchronized
Exemple : Préparer les données, puis verrouiller juste pour la mise à jour
- Éviter les opérations longues sous verrou
Pas d’I/O (fichiers, réseau, base de données)
Pas d’appels à des méthodes externes non contrôlées
Pas de calculs coûteux
## Conséquence
Sections critiques longues = contention élevée = mauvaises performances
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques38 / 46

Toujours libérer les verrous
Règle d’or avec Lock
Toujours libérer dans un bloc finally
## - Mauvais
1  lock.lock();
2  doSomething();
3  lock.unlock();
Si exception verrou jamais libéré!
## + Correct
1  lock.lock();
2  try {
3      doSomething();
4  } finally {
5      lock.unlock();
## 6  }
Libération garantie
## Note
Avec synchronized, la libération est automatique (même si exception)
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques39 / 46

Éviter les deadlocks
Conditions pour un interblocage
## 1
Exclusion mutuelle
## 2
Hold and wait (garder et attendre)
## 3
Pas de préemption
## 4
Attente circulaire
## Solutions
Ordre global : Toujours acquérir les verrous dans le même ordre
Timeout : Utiliser tryLock() avec timeout
Détection : Détecter et récupérer (complexe)
Éviter : Ne pas verrouiller plusieurs ressources si possible
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques40 / 46

Exemple : Deadlock et solution
- Deadlock possible
## 1  // Thread 1
2  synchronized(a) {
3    synchronized(b) {
4      transfer(a, b);
## 5    }
## 6  }
## 7
## 8  // Thread 2
9  synchronized(b) {
10    synchronized(a) {
11      transfer(b, a);
## 12    }
## 13  }
+ Ordre global
1  void transfer(Account from,
2                Account to) {
3    Account first, second;
4    if (from.id < to.id) {
5      first = from;
6      second = to;
7    } else {
8      first = to;
9      second = from;
## 10    }
11    synchronized(first) {
12      synchronized(second) {
## 13        // Transfer
## 14      }
## 15    }
## 16  }
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques41 / 46

Éviter les nested locks non contrôlés
## Anti-pattern
Acquérir plusieurs verrous imbriqués sans stratégie claire
## Recommandations
Limiter le nombre de verrous détenus simultanément
Documenter l’ordre d’acquisition
Utiliser des abstractions de plus haut niveau (ConcurrentHashMap, etc.)
Considérer des designs lock-free quand possible
Design alternatif
Au lieu de verrouiller plusieurs objets, utiliser un verrou unique de plus haut niveau
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques42 / 46

Préférer les collections concurrentes
java.util.concurrent
Collections thread-safe optimisées
Au lieu deUtiliser
HashMap synchroniséeConcurrentHashMap
ArrayList synchroniséeCopyOnWriteArrayList
LinkedList synchronisée    ConcurrentLinkedQueue
HashSet synchroniséConcurrentHashMap.newKeySet()
PriorityQueuePriorityBlockingQueue
## Avantages
Optimisées pour la concurrence (fine-grained locking)
Opérations atomiques intégrées
Pas de verrous externes nécessaires
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques43 / 46

ConcurrentHashMap : exemple
1  import java.util.concurrent.ConcurrentHashMap;
## 2
3  ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
## 4
5  // Opérations atomiques
6  map.put("key", 1);
7  map.putIfAbsent("key", 2);  // Atomique
8  map.computeIfAbsent("key", k -> expensiveComputation(k));
## 9
10  // Itération thread-safe (snapshot)
11  for (Map.Entry<String, Integer> entry : map.entrySet()) {
12      // Pas besoin de synchronisation
## 13  }
## 14
15  // Opérations atomiques composées
16  map.compute("counter", (k, v) -> v == null ? 1 : v + 1);
17  map.merge("counter", 1, Integer::sum);
## Note
ConcurrentHashMap utilise du lock striping (segmentation) pour de meilleures
performances
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques44 / 46

Hiérarchie de synchronisation
Pas de synchronisation
volatile
## Atomiques
## Lock
synchronized
Ordre de préférence (du bas vers le haut)
- Collections concurrentes / Atomiques
- synchronized (simple)
- Lock (flexibilité nécessaire)
- volatile (flags uniquement)
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques45 / 46

Récapitulatif : Quand utiliser quoi ?
BesoinSolution
Compteur simpleAtomicInteger
Flag d’arrêtvolatile boolean
Section critique simplesynchronized
Besoin de tryLock/timeoutReentrantLock
Map partagéeConcurrentHashMap
File producteur-consommateurBlockingQueue
Attendre N threadsCountDownLatch
Synchroniser N threadsCyclicBarrier
Limiter accès concurrentSemaphore
Coordination complexeLock + Condition
Synchronisation en Java, Verrous et Variables conditionnellesBonnes Pratiques46 / 46