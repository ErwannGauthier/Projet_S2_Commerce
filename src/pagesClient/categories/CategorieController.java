package pagesClient.categories;

import dbUtil.dbConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pageAccueil.AccueilController;
import pagesClient.ClientController;
import pagesClient.categories.produits.Produit;
import pagesClient.categories.produits.ProduitController;
import pagesClient.compte.CompteController;
import pagesClient.panier.PanierController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class CategorieController {

    private int idClient;
    private HashMap<Produit, Integer> panier;
    private String nomCategorie;
    private int borneMin;
    private int borneMax;
    private Collection<Produit> listeProduits;

    @FXML
    private AnchorPane fenetre;

    public void initDonnees(int id, HashMap<Produit, Integer> pan, Collection<Produit> liste, String nomCat, int borneMin, int borneMax) {

        this.idClient = id;
        this.panier = pan;

        if(nomCat.equals("Viandes et Poissons")){
            nomCat = "viandes_poissons";
        }
        else if(nomCat.equals("Fruits et Legumes")){
            nomCat = "fruits_legumes";
        }
        this.nomCategorie = nomCat;
        this.borneMin = borneMin;
        this.borneMax = borneMax;
        this.listeProduits = liste;

        afficheListe();
    }

    public void afficheListe(){

        Image img;
        int posX = 20;
        int posY = 45;
        Iterator<Produit> it = this.listeProduits.iterator();
        boolean peutAfficher;

        while(it.hasNext()){
            Produit produit = it.next();
            peutAfficher = true;

            if(produit.getId() > borneMin && produit.getId() < borneMax) {

                // Si l'utilisateur à déjà tout le stock d'un produit dans son panier, il ne peut plus le voir
                if(this.panier.containsKey(produit) && this.panier.get(produit) >= produit.getNbStock()){
                    peutAfficher = false;
                }
                else if(produit.getNbStock() <= 0){
                    peutAfficher = false;
                }

                if(peutAfficher == true){

                    try{
                        img = new Image("/images/categories/" + this.nomCategorie + "/" + produit.getId() + ".jfif", 100, 100, false, false);
                    }
                    catch(Exception ex){
                        img = new Image("/images/categories/notFound.jfif", 100, 100, false, false);
                    }
                    ImageView imageView = new ImageView(img);
                    imageView.setX(posX);
                    imageView.setY(posY);

                    Button bouton = new Button("Commander");
                    bouton.setMinSize(100, 20);
                    bouton.setLayoutX(posX);
                    bouton.setLayoutY(posY + 100);
                    bouton.setOnAction(ActionEvent -> {
                        ouvrePageProduit(produit);
                    });

                    this.fenetre.getChildren().addAll(imageView, bouton);

                    posX = posX + 120;
                    if(posX > 740){
                        posX = 20;
                        posY = posY + 150;
                    }
                }
            }
        }
    }

    public void ouvrePageProduit(Produit produit){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/produits/Produit.fxml"));

            Stage stageProduit = new Stage();
            stageProduit.setScene(new Scene(loader.load()));

            ProduitController produitController = loader.getController();

            produitController.initialiseDonnees(this.idClient, this.panier, this.listeProduits, produit, this.nomCategorie);

            stageProduit.setTitle("Commerce.io - Produit");
            stageProduit.setResizable(false);
            stageProduit.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }


    // ------- FONCTIONS POUR MENU -------
        // MON COMPTE
    @FXML
    private void consulterCompte(){

        if(this.idClient > 0){
            try{

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/compte/Compte.fxml"));

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));

                CompteController compteController = loader.getController();
                // Envoie l'ID du client dans le CompteController
                compteController.recupID(this.idClient);
                compteController.afficheDonnees();

                stage.setTitle("Commerce.io - Mon compte");
                stage.setResizable(false);
                stage.show();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deconnexion(){

        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pageAccueil/Accueil.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            AccueilController accueilController = loader.getController();

            stage.setTitle("Commerce.io - Accueil");
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        fermeFenetre();
    }

        // MON PANIER
        @FXML
        private void consulterPanier(){
            try{

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/panier/Panier.fxml"));

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));

                PanierController panierController = loader.getController();
                panierController.initDonnees(this.idClient, this.listeProduits, this.panier);

                stage.setTitle("Commerce.io - Panier");
                stage.setResizable(false);
                stage.show();
            }
            catch(IOException | SQLException e){
                e.printStackTrace();
            }

            fermeFenetre();
        }

        // CATEGORIES
    private static final int borneMinViandesPoissons = 10000;
    private static final int borneMaxViandesPoissons = 20000;
    private static final int borneMinFruitsLegumes = 20000;
    private static final int borneMaxFruitsLegumes = 30000;
    private static final int borneMinFrais = 30000;
    private static final int borneMaxFrais = 40000;
    private static final int borneMinSurgele = 40000;
    private static final int borneMaxSurgele = 50000;
    private static final int borneMinFeculents = 50000;
    private static final int borneMaxFeculents = 60000;
    private static final int borneMinConserves = 60000;
    private static final int borneMaxConserves = 70000;
    private static final int borneMinHygiene = 70000;
    private static final int borneMaxHygiene = 80000;
    private static final int borneMinBoissons = 80000;
    private static final int borneMaxBoissons = 90000;

    @FXML
    public void ouvrePrincipale(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie l'ID du client et le panier à la page Client
            clientController.recupDonnees(this.idClient, this.panier, this.listeProduits);

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void ouvreViandesPoissons(){
        fenetreGlobale("Viandes et Poissons", borneMinViandesPoissons, borneMaxViandesPoissons);
        fermeFenetre();
    }

    @FXML
    public void ouvreFruitsLegumes(){
        fenetreGlobale("Fruits et Legumes", borneMinFruitsLegumes, borneMaxFruitsLegumes);
        fermeFenetre();
    }

    @FXML
    public void ouvreFrais(){
        fenetreGlobale("Frais", borneMinFrais, borneMaxFrais);
        fermeFenetre();
    }

    @FXML
    public void ouvreSurgele(){
        fenetreGlobale("Surgele", borneMinSurgele, borneMaxSurgele);
        fermeFenetre();
    }

    @FXML
    public void ouvreFeculents(){
        fenetreGlobale("Feculents", borneMinFeculents, borneMaxFeculents);
        fermeFenetre();
    }

    @FXML
    public void ouvreConserves(){
        fenetreGlobale("Conserves", borneMinConserves, borneMaxConserves);
        fermeFenetre();
    }

    @FXML
    public void ouvreHygiene(){
        fenetreGlobale("Hygiene", borneMinHygiene, borneMaxHygiene);
        fermeFenetre();
    }

    @FXML
    public void ouvreBoissons(){
        fenetreGlobale("Boissons", borneMinBoissons, borneMaxBoissons);
        fermeFenetre();
    }

    public void fermeFenetre(){
        Stage stage = (Stage) this.fenetre.getScene().getWindow();
        stage.close();
    }

    public void fenetreGlobale(String nomCat, int borneMin, int borneMax){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/Categorie.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CategorieController categorieController = loader.getController();
            // Envoie des données permettant d'identifier la catégorie dans CategorieController
            categorieController.initDonnees(this.idClient, this.panier, this.listeProduits, nomCat, borneMin, borneMax);

            stage.setTitle("Commerce.io - Catégorie " + nomCat);
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
