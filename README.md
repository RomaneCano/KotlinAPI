# 🍊 KotlinAPI – Produits alimentaires, Nutri-Score, Scan & Boissons recommandées

Application Android développée en **Kotlin** avec **Jetpack Compose**, permettant de :

- 🔎 Rechercher des **produits alimentaires** depuis l’API **OpenFoodFacts**
- ⭐ Gérer une liste de **produits favoris** (persistés en local avec Room)
- 🅰️ Filtrer les produits selon leur **Nutri-Score (A → E)**
- 🧪 Consulter une **analyse nutritionnelle complète**
- 📸 Scanner un **QR Code ou un code-barres** pour retrouver un produit instantanément
- 🥤 Obtenir une **boisson recommandée** pour un produit, grâce à l’API **TheCocktailDB**
- 📱 Naviguer entre un **écran d’accueil**, un **détail produit**, un **écran favoris** et un **écran boisson**

---

## 📸 Aperçu des fonctionnalités

---

## 🏠 Écran d’accueil (Home)

- Champ de recherche filtrant les produits via OpenFoodFacts  
- Résultats affichés sous forme de cartes :
  - photo  
  - nom  
  - Nutri-Score codé couleur  
- Filtre **Nutri-Score** (A / B / C / D / E / Tous)
- Bouton **Scanner un produit** :
  - lecture de **QR codes**
  - lecture de **codes-barres** (EAN-13, EAN-8, UPC, Code 128…)
- Bouton CTA **« Découvrir un produit »** :
  - génère un produit **aléatoire**

---

## 📄 Écran détail produit

Pour chaque produit :

- 📷 Image du produit  
- 🏷 Nom, catégorie, origine  
- 🧾 Ingrédients  
- 🅰️ Nutri-Score affiché avec la bonne couleur  
- 🧪 Analyse nutritionnelle (énergie, graisses, sucres, sel, additifs…)  
- ⭐ Bouton « Ajouter / Retirer des favoris »  
- 🥤 Bouton **« Boisson recommandée »** :
  - extrait le premier ingrédient  
  - nettoyage des accents, virgules, parenthèses  
  - mapping FR → EN  
  - redirection vers l’écran boisson

---

## ⭐ Écran favoris

- Fond dégradé jaune/orange  
- **Carousel horizontal** pour naviguer entre les favoris  
- Bouton « Voir le produit »  
- Liste des favoris en dessous :
  - image + nom  
  - bouton poubelle pour supprimer  
- Message si la liste est vide :
  > "Aucun produit pour l'instant. Ajoutez des favoris depuis les fiches produits."

---

## 🥤 Écran boisson recommandée

- Requête à TheCocktailDB
- Transformation intelligente de l’ingrédient :  
  - suppression des accents  
  - retrait des parenthèses / virgules  
  - mapping automatique FR → EN
- Sélection **aléatoire** parmi les boissons possibles (évite d’avoir toujours la même)
- Affichage :  
  - nom  
  - image  
  - instructions  
- Gestion des erreurs :
  - spinner  
  - message si aucune boisson n’est trouvée  

---

## 📡 Scan QR / Code-barres

Fonctionnalité intégrée grâce à **ZXing** :

- Support QR Code  
- Support barres :  
  - EAN-13  
  - EAN-8  
  - UPC  
  - Code 128  
- Après le scan → navigation automatique vers :

---

## 🏗️ Architecture & organisation

L’application suit une structure claire et modulaire :

### **data/**
- `RecipeApiClient` → OpenFoodFacts  
- `DrinkApiClient` → TheCocktailDB  
- Parsing JSON (kotlinx + JSONObject)

### **model/**
- `Recipe`  
- `Drink`  
- `NutritionInfo`  
- Entité Room `FavoriteRecipeEntity`

### **database/**
- `AppDatabase`  
- `FavoriteRecipeDao`

### **repository/**
- `RecipeRepository`  
- `DrinkInfoRepository`

### **presentation/**
- `RecipeViewModel`  
- `FavoriteRecipeViewModel`  
- `DrinkInfoViewModel`

### **presentation/screen/**
- `HomeScreen`  
- `DetailScreen`  
- `FavoritesScreen`  
- `DrinkInfoScreen`

### **Navigation Compose**
- `home`  
- `detail/{id}/{name}`  
- `favorites`  
- `drinkInfo/{ingredient}`

---

## 🧰 Stack technique

- **Kotlin**
- **Jetpack Compose** (Material 3)
- **Navigation Compose**
- **Room** (DAO / Entity / Database)
- **Ktor Client**
- **kotlinx.serialization**
- **JSONObject** pour certains parsings
- **Coil** pour l’affichage des images
- **ZXing** pour le scan QR / code-barres
- **MVVM**
  - ViewModel + StateFlow
  - Coroutines + viewModelScope

---

## 🌐 APIs utilisées

### 🔎 OpenFoodFacts  
- Recherche  
  `https://fr.openfoodfacts.org/cgi/search.pl?search_terms=...&json=1&page_size=20`
- Détail  
  `https://fr.openfoodfacts.org/api/v2/product/{code}.json`

### 🍹 TheCocktailDB  
- Endpoint :  
  `https://www.thecocktaildb.com/api/json/v1/1/filter.php?i={ingredient}`

Logique interne :
- Filtres FR → EN pour maximiser les résultats  
- Sélection **aléatoire** de la boisson  
- Nettoyage d’ingrédient intelligent  

---

## 🎯 Conclusion

KotlinAPI est une application complète exploitant :

- la recherche en ligne  
- la recommandation intelligente  
- la persistance locale  
- la navigation moderne  
- le scan QR/code-barres  
- le Nutri-Score  
- une UI Jetpack Compose moderne  

