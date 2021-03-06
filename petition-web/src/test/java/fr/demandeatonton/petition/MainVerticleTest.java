package fr.demandeatonton.petition;

import java.util.List;

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
   private final static String PATH_GET_FIRST_PETITION = "/rest/get/0";
   private final static String EXPECTED_MESSAGE = "Hello World!";

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

   @Ignore
   public void canIAddAPetition(final TestContext context) {
      final Async async = context.async();
      // On créé un objet pétition
      final String NAME = "Petition contre la corruption";
      final String AUTHOR = "tonton@demandeatonton.fr";
      final String DESCRIPTION = "Il y a trop de corruption dans le monde bla bla bla";
      final int GOAL = 50;
      Petition petition = new Petition(NAME, AUTHOR, DESCRIPTION, GOAL);

      // On le convertit en Json et on l'envoit dans la requête POST
      String json = Json.encodePrettily(petition);
      RequestOptions ro = new RequestOptions();
      ro.setHost(HOST);
      ro.setPort(PORT);
      ro.setURI(PATH_ADD_PETITION);

      vertx.createHttpClient().post(ro).putHeader("Content-Length", String.valueOf(json.length()))
            .putHeader("Content-Type", "application/json").handler(response -> {
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
            List<Petition> lstPetitions = Json.decodeValue(body.toString(), List.class);
            context.assertTrue(lstPetitions.size() > 0);
            async.complete();
         });
      });
   }

   @Test
   public void canIRetrieveFirstPetition(final TestContext context) {
      final Async async = context.async();

      vertx.createHttpClient().getNow(PORT, HOST, PATH_GET_FIRST_PETITION, response -> {
         response.handler(body -> {
            context.assertEquals(response.statusCode(), 200);
            Petition petition = Json.decodeValue(body.toString(), Petition.class);
            context.assertEquals(petition.getId(), 0);
            async.complete();
         });
      });
   }

}
