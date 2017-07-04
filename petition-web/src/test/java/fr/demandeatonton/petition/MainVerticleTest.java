package fr.demandeatonton.petition;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.demandeatonton.petition.model.Petition;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

   private Vertx vertx;
   private final static int PORT = 8080;
   private final static String HOST = "localhost";
   private final static String PATH = "/";
   private final static String PATH_ADD_PETITION = "/rest/add";
   private final static String PATH_LIST_PETITIONS = "/rest/list";
   private final static String EXPECTED_MESSAGE = "Hello World!";
   private final static String EXPECTED_ADD_PETITION_MESSAGE = "Petition added";
   private final static String EXPECTED_LIST_PETITIONS_MESSAGE = "List of petitions";

   @Before
   public void before(final TestContext context) {
      vertx = Vertx.vertx();
      DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", PORT));
      vertx.deployVerticle(MainVerticle.class.getName(), options, context.asyncAssertSuccess());
   }

   @After
   public void after(final TestContext context) {
      vertx.close(context.asyncAssertSuccess());
   }

   @Test
   public void isTheFrontPageServed(final TestContext context) {
      final Async async = context.async();

      vertx.createHttpClient().getNow(PORT, HOST, PATH, response -> {
         response.handler(body -> {
            context.assertTrue(body.toString().contains(EXPECTED_MESSAGE));
            async.complete();
         });
      });
   }

   @Test
   public void canIAddAPetition(final TestContext context) {
      final Async async = context.async();
      // On créé un objet pétition
      final String NAME = "Petition contre la corruption";
      final String AUTHOR = "tonton@demandeatonton.fr";
      final int GOAL = 50;
      Petition petition = new Petition(NAME, AUTHOR, GOAL);

      // On le convertit en Json et on l'envoit dans la requête POST
      String json = Json.encodePrettily(petition);
      RequestOptions ro = new RequestOptions();
      ro.setHost(HOST);
      ro.setPort(PORT);
      ro.setURI(PATH_ADD_PETITION);

      System.out.println(json);
      vertx.createHttpClient().post(ro)
            .putHeader("Content-Length", String.valueOf(json.length()))
            .putHeader("Content-Type", "application/json")
            .handler(response -> {
               context.assertEquals(response.statusCode(), 201);
               context.assertTrue(response.headers().get("content-type").contains("application/json"));
               response.bodyHandler(body -> {
                  final Petition returnedPetition = Json.decodeValue(body.toString(), Petition.class);
                  context.assertEquals(petition, returnedPetition);
                  async.complete();
               });
            }).write(json).end();
   }

   @Test
   public void canIListExistingPetitions(final TestContext context) {
      final Async async = context.async();

      vertx.createHttpClient().getNow(PORT, HOST, PATH_LIST_PETITIONS, response -> {
         response.handler(body -> {
            context.assertEquals(response.statusCode(), 200);
            // List<Petition> listPetitions = Json.decodeValue(body.toString(), List.class);
            // context.assertTrue(listPetitions.size() > 0);
            async.complete();
         });
      });
   }

   @Ignore
   public void canIRetrieveFirstPetition(final TestContext context) {
      final Async async = context.async();

      vertx.createHttpClient().getNow(PORT, HOST, PATH_LIST_PETITIONS, response -> {
         response.handler(body -> {
            System.out.println(body.toString());
            context.assertTrue(body.toString().contains(EXPECTED_MESSAGE));
            async.complete();
         });
      });
   }

}
