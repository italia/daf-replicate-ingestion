package it.gov.daf.km4city;

public class Main {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create();
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Flow<HttpRequest, HttpResponse, CompletionStage<OutgoingConnection>> connectionFlow =
                Http.get(system).outgoingConnection(toHost("akka.io", 80));
        final CompletionStage<HttpResponse> responseFuture =
                // This is actually a bad idea in general. Even if the `connectionFlow` was instantiated only once above,
                // a new connection is opened every single time, `runWith` is called. Materialization (the `runWith` call)
                // and opening up a new connection is slow.
                //
                // The `outgoingConnection` API is very low-level. Use it only if you already have a `Source[HttpRequest]`
                // (other than Source.single) available that you want to use to run requests on a single persistent HTTP
                // connection.
                //
                // Unfortunately, this case is so uncommon, that we couldn't come up with a good example.
                //
                // In almost all cases it is better to use the `Http().singleRequest()` API instead.
                Source.single(HttpRequest.create("/"))
                        .via(connectionFlow)
                        .runWith(Sink.<HttpResponse>head(), materializer);
    }

}
