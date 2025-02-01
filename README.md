# Étude de Cas : Gestion des Commandes et Notifications avec JMS (Queue & Topic)
## Auteurs : Cyril & Enzo
## Date : *Pour le Samedi 1 février 2025*
## Technologies utilisées : Java, Spring Boot, JMS, ActiveMQ

---

## 1. Introduction
La messagerie asynchrone est une approche essentielle pour garantir l’évolutivité, la fiabilité et la résilience des systèmes distribués.
Dans le cadre de cette étude, nous avons conçu une architecture combinant **JMS Queue** (modèle point-à-point) et **JMS Topic** (modèle publish-subscribe) afin de répondre aux besoins d’un système de gestion des commandes et de notification.

Ce projet repose sur les concepts fondamentaux du **Java Message Service (JMS)** et sur l’utilisation d’**ActiveMQ** comme **message broker**.
Il illustre comment utiliser **les paradigmes Queue et Topic** pour orchestrer des communications asynchrones entre différentes entités d’un système.

---

## 2. Contexte et Objectifs
Dans une entreprise de commerce électronique, il est essentiel d’assurer un traitement efficace des commandes des clients tout en leur fournissant une notification instantanée une fois leur commande expédiée.

L’objectif de cette étude de cas est de concevoir un système où :
1. Un client passe une commande, qui est envoyée dans une **Queue JMS** (`orderQueue`).
2. Un service récupère la commande, la traite, puis publie une **notification dans un Topic JMS** (`notificationTopic`).
3. Tous les clients abonnés au `notificationTopic` reçoivent simultanément l'information.

Ce système doit respecter les **principes fondamentaux des systèmes de messagerie** :
- **Découplage des composants** : Le producteur de message (service client) et les consommateurs (service de commande et abonnés aux notifications) ne se connaissent pas directement.
- **Fiabilité** : Les messages doivent être transmis **sans perte** et être délivrés **une seule fois** à un consommateur dans le cas d’une queue.
- **Scalabilité** : Le système doit pouvoir supporter un **grand nombre de consommateurs et de producteurs** sans modification majeure de l’architecture.

---

## 3. Notions Théoriques et Concepts Appliqués

### 3.1. JMS (Java Message Service)
JMS est une **API Java standardisée** permettant aux applications de communiquer de manière **asynchrone** à travers des **messages** envoyés via un **message broker**. Deux **modèles d’échange** sont définis :
1. **JMS Queue (Point-to-Point)**
    - Un message est envoyé à une **file d’attente (queue)**.
    - Un **seul consommateur** récupère et traite le message.
    - Une fois le message consommé, il est retiré de la queue.
    - **Exemple d’usage** : Un service de gestion des commandes qui traite chaque commande individuellement.

2. **JMS Topic (Publish-Subscribe)**
    - Un message est envoyé à un **topic**.
    - **Tous les abonnés** du topic reçoivent une copie du message.
    - **Exemple d’usage** : Diffuser une notification de commande expédiée à tous les clients abonnés.

### 3.2. Middlewares Orientés Messages (MOM)
D'après le support académique **"Middlewares Orientés Messages (MOM)"**, les MOM sont des systèmes permettant une **communication asynchrone** entre applications. Ils se basent sur des **queues de messages persistantes** et assurent un **découplage temporel** entre émetteur et récepteur.

Les principaux concepts abordés sont :
- **Files de messages (Message Queueing)** : Assurent un acheminement fiable des messages entre applications.
- **Persistant vs. Transitoire** : Un message peut être conservé jusqu'à sa livraison ou perdu en cas de déconnexion de l’émetteur et du récepteur.
- **Modèle Publish-Subscribe** : Permet la diffusion à plusieurs consommateurs, réduisant ainsi le couplage applicatif.

### 3.3. Pourquoi l’approche hybride Queue + Topic ?
L’architecture de notre solution repose sur **la complémentarité des modèles Queue et Topic** :
- **La file d’attente (`orderQueue`) garantit que chaque commande est traitée une seule fois** par un service de gestion des commandes.
- **Le topic (`notificationTopic`) permet de notifier plusieurs clients simultanément**, assurant une diffusion efficace des mises à jour.

Cette approche permet de **modulariser** les composants et d’améliorer **la scalabilité** du système.

---

## 4. Approche Technique et Justifications

### 4.1. Modèle de communication
Le **flux de messages** se déroule en **trois étapes distinctes** :
1. **Le client passe une commande** → La commande est envoyée dans la `orderQueue`.
2. **Le service de commande récupère la commande depuis la queue et la traite**.
3. **Une notification est générée et publiée dans le `notificationTopic`**.
4. **Tous les clients abonnés au topic reçoivent la notification simultanément**.

### 4.2. Choix des Technologies
- **JMS (Java Message Service)** : API standard pour la messagerie asynchrone.
- **ActiveMQ** : Message broker permettant la gestion des queues et des topics.
- **Spring Boot** : Framework facilitant la gestion des beans et la configuration JMS.

### 4.3. Gestion des messages
- **Durabilité des messages** : En mode **AUTO_ACKNOWLEDGE**, les messages sont supprimés une fois consommés.
- **Traitement asynchrone** avec **MessageListener**, permettant aux consommateurs d'écouter les messages en continu.

---

## 5. Conclusion
Ce projet démontre la **complémentarité des modèles JMS Queue et Topic** dans la gestion d’un système transactionnel. L'utilisation d'ActiveMQ permet de garantir la **fiabilité des messages**, tandis que l'approche asynchrone assure **une communication fluide et efficace**.

Grâce aux notions de **messagerie asynchrone, découplage des services et scalabilité**, ce projet constitue une **base solide pour des applications industrielles et distribuées**.

## 6. Références

### Supports Académiques Utilisés :
1. **Introduction aux Architectures de Systèmes d’Information** - Salim Bouzitouna, basé sur les supports de Jacques Augustin & Reda Bendraou.
2. **Middlewares Orientés Messages (MOM)** - Ada Diaconescu, Télécom Paris.

Ces supports nous ont permis de mieux comprendre :
- L’importance des **architectures orientées services**.
- Les **avantages des MOM** pour le découplage des systèmes.
- Les **différentes topologies de message broker** et leur impact sur la performance et la scalabilité.

---

**Réalisé par :** Cyril & Enzo.
