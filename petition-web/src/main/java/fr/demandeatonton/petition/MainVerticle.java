package fr.demandeatonton.petition;

import fr.demandeatonton.petition.model.Petition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
   @Override
   public void start(Future<Void> fut) {
      // On va créer notre base de données temporaire
      createMemoryDatabase();

      // On créé un objet Router
      Router router = Router.router(vertx);

      // On bind "/" à notre message de bienvenue
      router.route("/").handler(routingContext -> {
         HttpServerResponse response = routingContext.response();
         response.putHeader("content-type", "text/html").end("Hello World!");
      });

      // On bind nos services REST
      router.route("/rest/list").handler(this::listPetitions);

      // On permet la lecture du corps de la requête pour toutes les routes commençant
      // par add
      router.route("/rest/add*").handler(BodyHandler.create());

      // On créé un chemin pour la création d'une pétition
      router.post("/rest/add").handler(this::addPetition);

      // On créé le serveur et on gère les requêtes entrantes avecnotre routeur
      vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
         if (result.succeeded()) {
            fut.complete();
         } else {
            fut.fail(result.cause());
         }
      });
   }

   private SQLClient getClient() {
      JsonObject config = new JsonObject()
            .put("url", "jdbc:hsqldb:mem:petition?shutdown=true")
            .put("driver_class", "org.hsqldb.jdbcDriver")
            .put("max_pool_size", 30);

      SQLClient client = JDBCClient.createShared(vertx, config);
      return client;
   }

   private void createMemoryDatabase() {
      System.out.println("Creating in memory database");
      SQLClient client = getClient();

      client.getConnection(res -> {
         if (res.succeeded()) {

            SQLConnection connection = res.result();

            connection.query(
                  "CREATE TABLE petitions (id int, name varchar(100), author varchar(100), description varchar(100), goal int)",
                  res2 -> {
                     if (!res2.succeeded()) {
                        throw new RuntimeException("Can't create in memory database");
                     }
                  });
         } else {
            throw new RuntimeException("Can't create in memory database");
         }
      });
   }

   private void addPetition(RoutingContext routingContext) {
      Petition petition = Json.decodeValue(routingContext.getBodyAsString(), Petition.class);
      System.out.println("Adding a petition");
      routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(petition));
   }

   private void listPetitions(RoutingContext routingContext) {
      System.out.println("Listing petitions");
      SQLClient client = getClient();

      client.getConnection(res -> {
         if (res.succeeded()) {

            SQLConnection connection = res.result();

            connection.query("SELECT * FROM petitions", res2 -> {
               if (res2.succeeded()) {

                  ResultSet rs = res2.result();
                  routingContext.response().setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(rs.toJson().encodePrettily());
               } else {
                  routingContext.response().setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end("error: can't connect to database");

               }
            });
         } else {
            routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
                  .end("error: can't connect to database");
         }
      });
   }
}
