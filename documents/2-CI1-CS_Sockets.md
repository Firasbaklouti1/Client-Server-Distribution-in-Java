

Programmation Concurrente et Distribuée
Modèle C/S, Programmation des sockets
## Khaled Barbaria
khaled.barbaria@ensta.org
Faculté des Sciences de Bizerte
Cycle Ingénieur, 1ère année
## 2025-2026
Modèle C/S, Programmation des sockets1 / 45

## Plan
## 1
Évolutions technologiques
## 2
L’Internet
## 3
Modèle Client/Serveur
## 4
## Programmtion Sockets
Sockets TCP
Sockets UDP
## Sockets Multicast
Modèle C/S, Programmation des socketsPlan2 / 45

Évolutions technologiques et nécessité de la
distribution
Évolutions technologiques
Développement de microprocesseurs puissants et peu couteux
En 1980 : la machine coûte 10
## 7
dollars et exécute une instruction
par seconde
En 2005 : la machine coûte 10
## 3
dollars et exécute 10
## 9
instructions
par seconde.
Développement des réseaux locaux et à large échelle
Les réseaux locaux permettent à une centaine de machines
d’échanger des données de petite taille en quelques
microsecondes (millisecondes)
Les réseaux à large échelle connectent des millions de machines
avec des vitesses variables de 64 Kbps (kilobits par seconde) à
plusieus gigabits par seconde.
Evolution très rapide des systèmes distribués en nombre,
technologies, impact économique et social ...
Modèle C/S, Programmation des socketsÉvolutions technologiques3 / 45

l’Internet
L’Internet
Système d’interconnexion de
machines utilisant un
ensemble standardisé de
protocoles de transfert de
données.
Réseau composé de millions
de réseaux aussi bien publics
que privés, universitaires,
commerciaux et
gouvernementaux.
Applications et services
variés : courrier électronique,
messagerie instantanée, Web,
etc.
## Figure 1 – Internet [http:
## //www.linux-france.org/]
Modèle C/S, Programmation des socketsL’Internet4 / 45

## Protocoles Internet I
## IP
IP se charge de
l’acheminement des paquets
pour tous les autres protocoles
de la famille TCP/IP.
Somme de contrôle du
protocole : confirme l’intégrité
de l’en-tête IP.
Mode non connecté,
c’est-à-dire que les paquets
émis par le niveau 3 sont
acheminés de manière
autonome (datagrammes),
sans garantie de livraison.
## Figure 2 – Protocoles [http:
## //www.linux-france.org/]
Modèle C/S, Programmation des socketsL’Internet5 / 45

Protocoles Internet II
## TCP
TCP fournit un protocole fiable (les paquets sont acquittés et
délivrés en séquence, leurs intégrité est vérifiée), orienté
connexion, au-dessus d’IP (ou encapsulé à l’intérieur d’IP).
Cette fiabilité fait de TCP/IP un protocole bien adapté pour la
transmission de données basée sur la session, les applications
client/serveur et les services critiques tels que le courrier
électronique.
Modèle C/S, Programmation des socketsL’Internet6 / 45

Protocoles Internet III
## UDP
Service de datagrammes sans connexion qui ne garantit ni la
remise ni l’ordre des paquets délivrés.
Sommes de contrôle des données facultatives
Permet d’échanger des données sur des réseaux à fiabilité élevée
sans utiliser inutilement des ressources réseau ou du temps de
traitement.
Prend également en charge l’envoi de données vers plusieurs
destinataires (multicast).
Modèle C/S, Programmation des socketsL’Internet7 / 45

Protocoles Internet IV
Protocoles applicatifs
Les protocoles du niveau application les plus connus sont :
HTTP (Hyper Text Transfer Protocol) permet l’accès aux
documents HTML et le transfert de fichiers depuis un site Web
FTP (File Transfer Protocol) pour le transfert de fichiers
Telnet pour la connexion à distance en émulation terminal, à un
hôte Unix/Linux.
SMTP (Simple Mail Transfer Protocol) pour la messagerie
électronique (sur UDP et TCP)
SNMP (Simple Network Management Protocol) pour
l’administration du réseau
NFS (Network File System) pour le partage des fichiers
Unix/Linux.
Modèle C/S, Programmation des socketsL’Internet8 / 45

Modèle client/serveur I
Modèle C/S
Se base sur la notion de service et décompose l’application en
deux parties : client et serveur
modèle Client/Serveur largement répandu
Serveurs Web, serveurs de temps, serveurs de mail, serveurs de
fichiers, serveurs de nommage (e.g. Domain Name Server)
..plein d’autres (transformer une chaîne de caractère en une autre,
convertir un fichier, effectuer des calculs exigeants en ressources,
etc.)
Modèle C/S, Programmation des socketsModèle Client/Serveur9 / 45

Modèle client/serveur II
Définition d’un modèle C/S d’une application
Pour définir une application client/serveur il faut préciser :
Protocole applicatif (l’ensemble des messages et leurs
enchaînements, càd quel message envoyer en réponse à chaque
message)
Protocole du transport (comment les données sont échangées ?
exemple TCP, UDP, ou autre)
Pseudo-code du client
Pseudo-code du serveur
Modèle C/S, Programmation des socketsModèle Client/Serveur10 / 45

Modèle client/serveur III
Exemple : calcul de la factorielle
Protocole applicatif : message m
## 1
: envoyé par le client contient
l’entier dont on souhaite connaître la factorielle. message m
## 2
## :
envoyé par le serveur contient une chaîne de caractères : soit le
résultat du calcul soit ERR en cas de problème.
Protocole de transport TCP (pour sa fiabilité).
Code client :
se connecter
envoyer m
## 1
attendre m
## 2
se deconnecter
afficher reponse
Code serveur :
loop(){
attendre_cnx
attendre m
## 1
calcul factorielle
envoyer m
## 2
deconnecter _client}
Ce modèle est très basique, il peut être enrichi avec des codes
d’erreurs : (ERRx où x est le code de l’erreur= 1 si le message est
mal formaté, 2 si l’entier négatif, 3 si l’entier est trop grand, 4 si
erreur au niveau du serveur, etc.).
Modèle C/S, Programmation des socketsModèle Client/Serveur11 / 45

Clients lourd et léger I
## Definition
Une application est dite en client léger si les principaux traitements se
font au niveau du serveur. Elle est en client lourd si le client prend en
charge l’intégralité du traitement et ne dépend du serveur que pour
l’échange des données.
Le client léger ne se charge que des aspects présentation et
potentiellement quelques traitements mineurs (exemple affichage
## HTML
Le client lourd prend généralement en charge l’intégralité du
traitement (exemples : client avec des capacités d’affichage
poussées (3D, GUI), clients utilisant les serveurs uniquement pour
stocker les données, Outlook).
Modèle C/S, Programmation des socketsModèle Client/Serveur12 / 45

Clients lourd et léger II
Client légerClient lourd
AutonomieDépendant    du    ser-
veur
## Grandeautonomie
même si serveur indis-
ponible
Ressources    né-
cesaires
LimitéesBonne à forte exploita-
tion
Fluidité   d’exécu-
tion
Dépendante    du    ré-
seau
Dépendante   des   res-
sources
MaintenanceSimpleplus compliquée
Modèle C/S, Programmation des socketsModèle Client/Serveur13 / 45

État d’un serveur
Definition (Serveur avec et serveur sans état)
Un serveur est dit sans état s’il traite chaque requête comme une
opération indépendante (aucun lien avec les requêtes précédentes). Il
est avec état s’il met à jour, d’une façon durable, des variables internes
suite au requêtes des clients.
Sans étatAvec état
Conception   simple   du   ser-
veur
Gestion de l’état nécessaire
Nécessite d’extraire l’informa-
tion sur le client depuis sa re-
quête
Nécessite de mettre à jour les
informations sur les clients
Tolérance aux fautes simpleNécessite   de   synchroniser
les états entre les répliques
Modèle C/S, Programmation des socketsModèle Client/Serveur14 / 45

## Sockets
Introduites par BSD (Berkeley Software Distribution)
Permettent la communication entre les processus
Processus sur la même machine
Processus connectés par un réseau
Le protocole réseau (couche 3) utilisé est IP (Internet Protocol)
Les sockets permettent d’utiliser deux protocoles de transport
## (couche 4)
Transmission Control Protocol (TCP)
User Datagram Protocol (UDP) : unicast et multicast
Modèle C/S, Programmation des socketsProgrammtion Sockets15 / 45

Protocole TCP I
Orienté connexion (Analogie avec l’appel téléphonique)
la première personne compose le numéro, attend que la seconde
personne décroche : la connexion est alors établie, elle se termine
lorsqu’une personne raccroche. La connexion prévue par TCP est
durable
Remarquer l’utilisation de InputStream et OutputStream
Protocole fiable (réemission jusqu’à ce que le receveur acquitte
l’arrivée des données)
Contrôle de flux : empêche l’émetteur de submerger le receveur
avec les données
Contrôle de congestion : empêche l’émetteur de saturer le réseau
Modèle C/S, Programmation des socketsProgrammtion Sockets16 / 45

Protocole TCP II
Modèle C/S, Programmation des socketsProgrammtion Sockets17 / 45

Protocole TCP III
Coté serveur
Créer une socket Serveur ServerSocket qui attend les
connexions. Le numéro de port d’écoute est spécifié ici.
La demande de connexion du client cause la création d’une
nouvelle socket.
Coté client
Utiliser le constructeur Socket avec (@IP, port) du serveur.
Appel bloquant : une fois réussi on peut commencer l’échange
des données
=> Il ne reste plus qu’à écrire et lire depuis et vers les flux (streams).
Modèle C/S, Programmation des socketsProgrammtion Sockets18 / 45

Protocole TCP IV
Classe ServerSocket (coté serveur)
Constructeur : ServerSocket (int port) : crée une socket TCP qui
attend les connexions au port port.
Principales méthodes : accept() bloque jusqu’à l’arrivée d’une
connexion client, renvoie une nouvelle socket pour dialoguer avec
le client. close() ferme la socket
Classe Socket (coté client et coté serveur)
Constructeur : Socket (string host, int port) : crée une socket TCP
connectée à l’hôte spécifié.
Principales méthodes : getOutputStream() renvoie un flux de
sortie permettant d’écrire vers cette socket ;  getInputStream()
renvoie un flux d’entrée permettant de lire depuis cette socket ;
close() ferme la socket.
Modèle C/S, Programmation des socketsProgrammtion Sockets19 / 45

Flux d’entrée : InputStream
InputStream : flux d’entrée lisible octet par octet (read()).
La méthode read permet de lire le prochain octet du flux.
L’octet est renvoyé sous forme d’entier entre 0 et 255 (-1 s’il
n’existe plus d’octet)
read bloque jusqu’à ce qu’il y ait des données, que la fin du flux
soit détectée ou qu’une exception est levée.
1I n p u t S t r e a m   i s t r e a m =
2new  D a t a I n p u t S t r e a m ( c o n n e c t i o n S o c k e t . g e t I n p u t S t r e a m ( ) ) ;
## 3
4i n t   d a t a = 0 ;
5while ( ( d a t a = i s t r e a m . read ( ) ) ! = − 1 )
6System . o u t . p r i n t ( ( char ) d a t a ) ;
7i s t r e a m . c l o s e ( ) ;
Modèle C/S, Programmation des socketsProgrammtion Sockets20 / 45

Flux de sortie : OutputStream
OutputStream : flux de sortie
inscriptible octet par octet grâce à la méthode write.
La méthode flush permet de forcer l’écriture des octets vers le flux.
1byte   [ ]   b  =new  byte [ 1 2 8 ] ;   b = . . . ;
2Socket   s  =   . . . . ;
## 3
4OutputStream   t h e O u t p u t   =  s . g e t O u t p u t S t r e a m ( ) ;
5t h e O u t p u t . w r i t e ( b ,   0 ,   b . l e n g t h ) ;
6/ / ou   encore
7f o r ( i n t   i = 0 ;   i <b . l e n g t h ;   i ++)
8t h e O u t p u t . w r i t e   ( b [ i ] ) ;
## 9
10s . c l o s e ( ) ;
Modèle C/S, Programmation des socketsProgrammtion Sockets21 / 45

Communications par flux de string
Modèle C/S, Programmation des socketsProgrammtion Sockets22 / 45

Communications basées sur la sérialisation
Modèle C/S, Programmation des socketsProgrammtion Sockets23 / 45

Exemple de serveur TCP
1import   j a v a . n e t . Socket ;
2import   j a v a . n e t . S e r v e r S o c k e t ;
3import   j a v a . i o .
## *
## ;
4import   j a v a . u t i l . Date ;
5p u b l i c   class   EchoServer   {
6p u b l i c   s t a t i c   void   main ( S t r i n g [ ]   a r g s )   throws   E x c e p t i o n   {
7S e r v e r S o c k e t   s e r v e r S o c k e t   =  new  S e r v e r S o c k e t ( 4 4 4 4 ) ;
8while   ( t r u e )   {
9System . o u t . p r i n t l n ( " En   a t t e n t e   de   c o n n e x i o n " ) ;
10Socket   c l i e n t S o c k e t   =   s e r v e r S o c k e t . a c c e p t ( ) ;
11B u f f e r e d R e a d e r   i n=  new  B u f f e r e d R e a d e r
12( new  InputStreamReader   ( c l i e n t S o c k e t . g e t I n p u t S t r e a m ( ) ) ) ;
13P r i n t W r i t e r   o u t   =  new   P r i n t W r i t e r ( c l i e n t S o c k e t . g e t O u t p u t S t r e a m ( ) ) ;
14S t r i n g   s  =   i n . r e a d L i n e ( ) ;
15System . o u t . p r i n t l n ( " S e r v e r   r e c e i v e d   :   " +s ) ;
16o u t . p r i n t l n ( " S e r v e r   r e p l i e d   :   " +s ) ;
17o u t . p r i n t l n ( new  Date ( ) . t o S t r i n g ( ) ) ;
18o u t . f l u s h ( ) ;
19o u t . c l o s e ( ) ;   i n . c l o s e ( ) ;   c l i e n t S o c k e t . c l o s e ( ) ;
## 20}
## 21}
## 22}
Modèle C/S, Programmation des socketsProgrammtion Sockets24 / 45

Exemple de client TCP
## 1
2import   j a v a . n e t . Socket ;
3import   j a v a . i o .
## *
## ;
4p u b l i c   class   E c h o C l i e n t   {
5p u b l i c   s t a t i c   void   main ( S t r i n g [ ]   a r g s )   throws   E x c e p t i o n   {
6S t r i n g   h o s t   =   " l o c a l h o s t " ;i n t   p o r t   =  4444;
7Socket   s o c k e t   =  new  Socket ( h o s t ,   p o r t ) ;
8B u f f e r e d R e a d e r   i n=  new  B u f f e r e d R e a d e r ( new  InputStreamReader
9( s o c k e t . g e t I n p u t S t r e a m ( ) ) ) ;
10P r i n t W r i t e r   o u t   =  new   P r i n t W r i t e r ( s o c k e t . g e t O u t p u t S t r e a m ( ) ) ;
11System . e r r . p r i n t l n ( " Connected   t o   "   +   h o s t   +   "   on   p o r t   "   +   p o r t ) ;
12Thread . s l e e p ( 5 0 0 0 ) ;
13o u t . p r i n t l n ( " I ’m  t h e   C l i e n t " ) ;   o u t . f l u s h ( ) ;
14System . o u t . p r i n t l n ( i n . r e a d L i n e ( ) ) ;
15System . o u t . p r i n t l n ( i n . r e a d L i n e ( ) ) ;
16o u t . c l o s e ( ) ;   i n . c l o s e ( ) ;   s o c k e t . c l o s e ( ) ;
## 17}
## 18}
Modèle C/S, Programmation des socketsProgrammtion Sockets25 / 45

## Exercices
Programmer un serveur (TCP) qui renvoie une chaîne de
caractères qui renvoie la date et l’heure dès qu’un client se
connecte. Les données envoyées par le client ne sont pas traitées.
Le serveur ferme la connexion juste après envoi de la chaîne
contenant la date. Tester le serveur en utilisant deux méthodes.
Programmer un client qui permet de trouver les ports occupés par
des serveurs TCP sur une machine distante donnée.
Programmer un serveur qui permet de trouver les ports occupés
par des serveurs TCP sur la machine locale.
Modèle C/S, Programmation des socketsProgrammtion Sockets26 / 45

Serveur de date TCP
1import   j a v a . l a n g .
## *
; import   j a v a . i o .
## *
## ;
2import   j a v a . n e t .
## *
; import   j a v a . u t i l . Date ;
3class   D a t e S e r v e r   {
4p u b l i c   s t a t i c   void   main ( S t r i n g   a r g s [ ] )   throws   E x c e p t i o n   {
5S e r v e r S o c k e t   s r v r   =  new  S e r v e r S o c k e t ( 1 2 3 5 ) ;
6f o r ( i n t   i = 0 ;   i < 3 ; i ++) {
7Socket   s k t   =   s r v r . a c c e p t ( ) ;
8P r i n t W r i t e r   o u t   =  new   P r i n t W r i t e r ( s k t . g e t O u t p u t S t r e a m ( ) ,   t r u e ) ;
9o u t . p r i n t l n ( new  Date ( ) . t o S t r i n g ( ) ) ;
10o u t . f l u s h ( ) ;
11o u t . c l o s e ( ) ;   s k t . c l o s e ( ) ;
## 12}
13s r v r . c l o s e ( ) ;
## 14}
## 15}
Modèle C/S, Programmation des socketsProgrammtion Sockets27 / 45

Client pour le serveur de date
1import   j a v a . l a n g .
## *
;   import   j a v a . i o .
## *
;   import   j a v a . n e t .
## *
## ;
## 2
3class   D a t e C l i e n t   {
4p u b l i c   s t a t i c   void   main ( S t r i n g   a r g s [ ] )   throws   E x c e p t i o n {
5Socket   s k t   =  new  Socket ( " l o c a l h o s t " ,   1235) ;
6B u f f e r e d R e a d e r   i n   =  new  B u f f e r e d R e a d e r
7( new  InputStreamReader ( s k t . g e t I n p u t S t r e a m ( ) ) ) ;
8System . o u t . p r i n t l n ( i n . r e a d L i n e ( ) + " \ n " ) ;
9i n . c l o s e ( ) ;
## 10}
## 11}
Il est également possible d’utiliser telnet
Modèle C/S, Programmation des socketsProgrammtion Sockets28 / 45

Applications et limites de TCP
## Applications
TCP est utilisé par la majorité des protocoles (applicatifs) tels que
HTTP, FTP, SMTP et Telnet qui demandent la fiabilité offerte par
ce protocole.
Les clients Web utilisent HTTP ou FTP pour télécharger d’une
manière fiable depuis un serveur Web.
Les utilisateurs du courrier électronique nécessitent la fiabilité de
SMTP pour l’emission des emails.
## Limites
Le contrôle le de flux et de congestion causent des problèmes de
comportement temporel :
performances : temps de transfert
déterminisme : distribution des temps de transfert par rapport à la
moyenne.
TCP n’est donc pas adapté aux applications temps réel
Modèle C/S, Programmation des socketsProgrammtion Sockets29 / 45

Le protocole UDP
Protocole non connecté (analogue à une communication par
courrier). L’adresse de destination est nécessaire à chaque envoi,
et aucun accusé de réception (acquittement) n’est donné.
Pas de garanties de fiabilité (Best effort)
Meilleur comportement temporel que TCP (performance et
déterminisme)
Bien adapté à la diffusion de vidéo/audio o‘u le comportement
temporel est important et la la perte de quelques paquets ne pose
pas problème
Modèle C/S, Programmation des socketsProgrammtion Sockets30 / 45

Programmation des sockets UDP en java I
Classe DatagramPacket
Représente un paquet qui contient toute l’information nécessaire à sa
transmission entre les points de connexion.
Constructeur : DatagramPacket(byte[] buf, int length) construit un
paquet pour recevoir des paquets de taille maximale length.
DatagramPacket(byte[] buf, int length, InetAddress address, int
port) construit un paquet pour envoyer un tableau d’octets.
Principales méthodes : getLength(), getData(), getPort(),
getAddress().
Modèle C/S, Programmation des socketsProgrammtion Sockets31 / 45

Programmation des sockets UDP en java II
Classe DatagramSocket
Constructeur : DatagramSocket(int port) construit une socket et
l’attache au port spécifié de la machine locale.
Principales méthodes : getAddress et getPort renvoient l’adresse
de la socket. getLocalPort renvoie le numéro de port d’attache de
la socket au niveau du hôte local. send(DatagramPacket p) et
receive(DatagramPacket p) pour envoyer et recevoir les paquets
datagrammes.
Modèle C/S, Programmation des socketsProgrammtion Sockets32 / 45

Serveur UDP
1import   j a v a . i o .
## *
;   import   j a v a . n e t .
## *
## ;
## 2
3class   UDPServer {
4p u b l i c   s t a t i c   void   main ( S t r i n g   a r g s [ ] )   throws   E x c e p t i o n {
5DatagramSocket  UDPSock  =  new  DatagramSocket ( 9 8 7 6 ) ;
6byte [ ]   r d   =  new  byte [ 1 0 2 4 ] ;
7byte [ ]   sd  =  new  byte [ 1 0 2 4 ] ;
8whil e ( t r u e ) {
9DatagramPacket   r e c e i v e P a c k e t
10=  new  DatagramPacket ( rd ,   r d . l e n g t h ) ;
11UDPSock . r e c e i v e ( r e c e i v e P a c k e t ) ;
12S t r i n g   sentence  =  new   S t r i n g (   r e c e i v e P a c k e t . g e t D a t a ( ) ) ;
13System . o u t . p r i n t l n ( " RECEIVED :   "   +  sentence ) ;
14I n e t A d d r e s s   IPAddress  =   r e c e i v e P a c k e t . g e t A d d r e s s ( ) ;
15i n t   p o r t   =   r e c e i v e P a c k e t . g e t P o r t ( ) ;
## 16
17sd  =  sentence . toUpperCase ( ) . g e t B y t e s ( ) ;
18DatagramPacket   sendPacket  =
19new  DatagramPacket ( sd ,   sd . l e n g t h ,   IPAddress ,   p o r t ) ;
20UDPSock . send ( sendPacket ) ;
## 21}
## 22}
## 23}
Modèle C/S, Programmation des socketsProgrammtion Sockets33 / 45

Client UDP
1import   j a v a . i o .
## *
;   import   j a v a . n e t .
## *
## ;
## 2
3p u b l i c   class   UDPClient   {
4p u b l i c   s t a t i c   void   main ( S t r i n g   a r g s [ ] )   throws   E x c e p t i o n   {
5B u f f e r e d R e a d e r   inFromUser  =
6new  B u f f e r e d R e a d e r ( new  InputStreamReader ( System . i n ) ) ;
7DatagramSocket  UDPSock  =  new  DatagramSocket ( ) ;
8I n e t A d d r e s s   IPAddress  =   I n e t A d d r e s s . getByName ( " l o c a l h o s t " ) ;
9byte [ ]   sd  =  new  byte [ 1 0 2 4 ] ; byte [ ]   r d   =  new  byte [ 1 0 2 4 ] ;
10S t r i n g   sentence  =  inFromUser . r e a d L i n e ( ) ;
11sd  =  sentence . g e t B y t e s ( ) ;
12DatagramPacket   sendPacket  =
13new  DatagramPacket ( sd ,   sd . l e n g t h ,   IPAddress ,   9876) ;
14UDPSock . send ( sendPacket ) ;
15DatagramPacket   r e c e i v e P a c k e t   =
16new  DatagramPacket ( rd ,   r d . l e n g t h ) ;
17UDPSock . r e c e i v e ( r e c e i v e P a c k e t ) ;
## 18
19System . o u t . p r i n t l n ( "FROM  SERVER : "
20+  new   S t r i n g ( r e c e i v e P a c k e t . g e t D a t a ( ) ) ) ;
21UDPSock . c l o s e ( ) ;
## 22}
## 23}
Modèle C/S, Programmation des socketsProgrammtion Sockets34 / 45

## Sockets Multicast
## Mulicast
Entre le Broadcast et la communication point à point
Les membres expriment leur intérêt en joignant un groupe
multicast (designé par une adresse IP entre 224.0.0.0 et
## 239.255.255.255)
On utilise le TTL pour limiter la portée des packets (protection des
bansdes passante)
MulticastSocket
Constructteur new MulticastSocket(nport)
Joindre un groupe joinGroup(InetAddress mcastaddr)
Envoyer les données aux membres du groupe
send(DatagramPacket dp, byte ttl)
Recevoir les données destinées au groupe (idem UDP)
quitter le groupe leaveGroup(InetAddress mcastaddr)
Modèle C/S, Programmation des socketsProgrammtion Sockets35 / 45

Serveur Multicast (Subsribers)
1p u b l i c   class   M u l t i c a s t S e r v e r {
2p u b l i c   s t a t i c   void   main ( S t r i n g [ ]   a r g s )   throws   E x c e p t i o n   {
3i n t   p o r t   =  5000;   S t r i n g   group  =   " 2 2 5 . 4 . 5 . 6 " ;
## 4
5M u l t i c a s t S o c k e t   s  =  new  M u l t i c a s t S o c k e t ( p o r t ) ;
6/ /    j o i n   t h e   m u l t i c a s t   group
7s . j o i n G r o u p ( I n e t A d d r e s s . getByName ( group ) ) ;
## 8
9/ /   C r e a t e   a  DatagramPacket   and  do  a   r e c e i v e
10byte   [ ]   b u f   =  newbyte [ 1 0 2 4 ] ;
11DatagramPacket   pack  =  new  DatagramPacket ( buf ,   b u f . l e n g t h ) ;
12f o r   ( i n t   i = 0 ; i < 5 ; i ++) {
13s . r e c e i v e ( pack ) ;
## 14
15System . o u t . p r i n t l n ( " Received   d a t a   from :   "   +
16pack . g e t A d d r e s s ( ) . t o S t r i n g ( )   +
17" : "   +  pack . g e t P o r t ( ) +new   S t r i n g ( pack . g e t D a t a ( ) ) ) ;
## 18}
19s . leaveGroup ( I n e t A d d r e s s . getByName ( group ) ) ;
20s . c l o s e ( ) ;
## 21}
## 22}
Modèle C/S, Programmation des socketsProgrammtion Sockets36 / 45

Client Multicast (Publisher)
1p u b l i c   class   M u l t i c a s t C l i e n t {
2p u b l i c   s t a t i c   void   main   ( S t r i n g [ ] a r g s )   throws   E x c e p t i o n   {
3i n t   p o r t   =  5000;S t r i n g   group  =   " 2 2 5 . 4 . 5 . 6 " ;
4i n t    t t l   =   1 ;
## 5
6M u l t i c a s t S o c k e t   s  =  new  M u l t i c a s t S o c k e t ( ) ;
7/ /   No  need   t o   j o i n   a   group
## 8
9byte   b u f [ ]   =  new   S t r i n g ( " B o n j o u r " ) . g e t B y t e s ( ) ;
10/ /   C r e a t e   a  DatagramPacket
11DatagramPacket   pack  =  new  DatagramPacket
12( buf ,   b u f . l e n g t h ,   I n e t A d d r e s s . getByName ( group ) ,   p o r t ) ;
## 13
14/ /   Sending   Data ,    t t l   e s t   o f   t y p e   b y t e .
15s . send ( pack , ( byte ) t t l ) ;
16s . c l o s e ( ) ;
## 17}
## 18}
Modèle C/S, Programmation des socketsProgrammtion Sockets37 / 45

Séparation des préoccupations I
Principe de concpetion
Séparer l’application en modules de telle sorte que chaque module a
une préoccupation distincte
Permet de faire évoluer les aspects hortogonaux (péoccpations
distinctes) indépendamment les uns des autres.
Modèle C/S, Programmation des socketsProgrammtion Sockets38 / 45

Séparation des préoccupations II
Exemple : Séparer les communications des aspects
applicatifs (aspects métier)
Les aspects applicatifs peuvent facilement être gérés par la
programmation Orientée Objet
Les communications reviennent à envoyer/recevoir des bits
La sérialisation permet de faire la conversion entre les objets et
les bits d’une manière générique (en java : il suffit que les objets
implémentent Serializable)
Modules responsable des communications : intergiciels
## (middleware).
Modèle C/S, Programmation des socketsProgrammtion Sockets39 / 45

## Sérialisation Java I
## Sérialisation
La sérialisation est un mécanisme qui convertit un objet en une
séquence d’octets. Les octets représentent fidèlement l’objet sérialisé :
principalement son état ainsi que quelques informations relatives au
type de l’objet et à sa structure. Ceci permet de reconstituer l’objet :
c’est la dé-sérialisation.
## Avantages
Permet de stocker les objets dans des fichiers, des bases de
données, de les envoyer sur le réseau, etc.
Portabilité (Indépendance de la machine) : tout objet sérialisé sur
une machine peut être dé-sérialisé sur une autre machine.
Héritage : Si un objet est sérialisable alors tous ses desendants
sont aussi sérialisables.
Modèle C/S, Programmation des socketsProgrammtion Sockets40 / 45

Sérialisation Java II
## Remarques
Les octes sérialisés représentent une copie de l’état de l’objet au
moment de la sérialisation, si l’objet change d’état, il faut le
sérialiser de nouveau.
Si la classe de l’objet n’implémente pas l’interface sérialisable
alors il y aura un problème : (une exception, ou prise en compte
uniquement de l’adresse de l’objet)
Modèle C/S, Programmation des socketsProgrammtion Sockets41 / 45

Sérialisation Java III
1import   j a v a . i o .
## *
## ;
2class   Person   implements   j a v a . i o . S e r i a l i z a b l e {
3S t r i n g   f i r s t N a m e ; S t r i n g   lastName ; i n t   age ;
## 4
5Person ( S t r i n g   _ f i r s t N a m e ,   S t r i n g   _lastName ,   i n t   _age ) {
6f i r s t N a m e = _ f i r s t N a m e ;   lastName=_lastName ;   age=_age ;
## 7}
## 8
9p u b l i c   s t a t i c   void   main ( S t r i n g   a r g s [ ] ) throws   E x c e p t i o n {
10Person  p=new  Person ( " A l i " , " Hamdi " , 2 5 ) ;
11p . p r i n t ( ) ;
12/ /   C o n v e r s i o n   d ’ un   o b j e t   Person  en   b y t e [ ]
13B y t e A r r a y O u t p u t S t r e a m   bos  =  new  B y t e A r r a y O u t p u t S t r e a m ( ) ;
14O b j e c t O u t p u t S t r e a m   oos  =  new  O b j e c t O u t p u t S t r e a m ( bos ) ;
15oos . w r i t e O b j e c t   ( p ) ;
16byte [ ]   d a t a   =  bos . t o B y t e A r r a y ( ) ;
## 17
18/ /   c o n v e r s i o n   d ’ un   b y t e [ ]   en  Person
19B y t e A r r a y I n p u t S t r e a m   b i s   =  new  B y t e A r r a y I n p u t S t r e a m ( d a t a ) ;
20O b j e c t I n p u t S t r e a m   o i s   =  new  O b j e c t I n p u t S t r e a m ( b i s ) ;
21Person   d e s e r i a l i z e d P r e s o n   =   ( Person )   o i s . r e a d O b j e c t ( ) ;
22d e s e r i a l i z e d P r e s o n . p r i n t ( ) ;
## 23}
Modèle C/S, Programmation des socketsProgrammtion Sockets42 / 45

Encapsuler les descripteurs de flux
## Idée
Exploiter l’héritage pour simplifier le code
Il suffit de passer des objets Socket valides dans les
constructeurs des classes In et Out.
1import   j a v a . i o .
## *
; import   j a v a . n e t .
## *
## ;
## 2
3p u b l i c   class   I n   extends   B u f f e r e d R e a d e r   {
4p u b l i c   I n   ( Socket   s )   throws   I O E x c e p t i o n {
5super   ( new  InputStreamReader ( s . g e t I n p u t S t r e a m ( ) ) ) ;
## 6}
## 7
8p u b l i c   I n   ( ) throws   I O E x c e p t i o n {
9super ( new  InputStreamReader ( System . i n ) ) ;
## 10}
## 11}
1import   j a v a . i o .
## *
; import   j a v a . n e t .
## *
## ;
## 2
3p u b l i c   class   Out   extends   P r i n t W r i t e r   {
4p u b l i c   Out   ( Socket   s )   throws   I O E x c e p t i o n {
5super   ( s . g e t O u t p u t S t r e a m ( ) ) ;
## 6}
## 7}
Modèle C/S, Programmation des socketsProgrammtion Sockets43 / 45

## Conclusions I
Développement des applications réparties
Facilité par la notion de Sockets et par les API fournies par les
langages de programmation (Par exemple java.net pour le
langage Java)
Les protocoles TCP, UDP et multicast sont suffisants pour la
majorité des applications. Le choix du bon protocole dépend des
besoins des applications
Il est important de séparer les aspects hortognaux : Aspects
métiers, Communications, Gestion de la concurrence, etc.
Ceci permet d’obtenir des propriétés désirables : modularité,
portabilité, interopérabilité
La sérialisabilié a un apport dans ce sens
Les intergiciels (middleware) proposent des solutions plus
complètes
Modèle C/S, Programmation des socketsProgrammtion Sockets44 / 45

Conclusions II
## Remarque
Les appels bloquants (accept sur ServerSocket, receive sur
DatagramSocket, readLine et readObject) facilitent la
programmation, mais blocage possible si la partie distante
n’envoie pas les données requises.
Nécessité d’avoir la possibilté de lancer plusieurs tâches, de
lancer des timeouts, d’abondonnner certaines tâches, etc.
## Programmation Concurrente !
Modèle C/S, Programmation des socketsProgrammtion Sockets45 / 45