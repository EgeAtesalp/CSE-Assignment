package hipstershop;

import hipstershop.Demo.Comments;
import hipstershop.Demo.AddCommentRequest;
import hipstershop.Demo.Comment;
import hipstershop.Demo.GetCommentRequest;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Duration;
import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsConfiguration;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.opencensus.exporter.trace.jaeger.JaegerExporterConfiguration;
import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CommentService extends hipstershop.CommentServiceGrpc.CommentServiceImplBase {

    private static final Logger logger = LogManager.getLogger(CommentService.class); //                  //

    private static HashMap<String, ArrayList<Comment>> commentList;

    public CommentService(){
        commentList = new HashMap<>();
        ArrayList<Comment> comments = new ArrayList<>();
        Comment.Builder builder1 = Comment.newBuilder().setProductId("2ZYFJ3GM2N").setUserName("Tom").setCommentText("Liked it!!!").setDate(new Date(System.currentTimeMillis()).toString()).setStars(5);
        comments.add(builder1.build());
        Comment.Builder builder2 = Comment.newBuilder().setProductId("2ZYFJ3GM2N").setUserName("TheOneWhoHates").setCommentText("Hated it!!!").setDate(new Date(System.currentTimeMillis()).toString()).setStars(0);
        comments.add(builder2.build());
        commentList.put("2ZYFJ3GM2N", comments);
        logger.info("new initiation, default comments added");
    }

    @Override
    public void getComment(GetCommentRequest request, StreamObserver<Comments> comments) {
        String pId = request.getProductId();
        List<Comment> commentList = getCommentsFromList(pId);

        Comments.Builder builder = Comments.newBuilder();

        builder.addAllComment(commentList);

        comments.onNext(builder.build());
        comments.onCompleted();
    }

    @Override 
    public void addComment(AddCommentRequest request, StreamObserver<Comments> observer){
        Comment newComment = request.getComment();
        String pId = newComment.getProductId();

        saveComments(pId, newComment);

        Comments.Builder builder = Comments.newBuilder();

        builder.addAllComment(commentList.get(pId));

        observer.onNext(builder.build());
        observer.onCompleted();
    }

    private static List<Comment> getCommentsFromList(String productId) {
        ArrayList<Comment> commentsForId = commentList.get(productId);

        if(commentsForId == null){
            commentsForId = new ArrayList<>();
            logger.warn("No comments found in " + productId);
        } 

        return commentsForId;
    }

    private synchronized static void saveComments(String pId, Comment comment) {

        ArrayList<Comment> newComments = commentList.get(pId);

        if(newComments == null) {
            newComments = new ArrayList<>();
            newComments.add(comment);
            logger.info("added new comment " + comment);
            commentList.put(pId, newComments);
        } else {
            newComments.add(comment);
            logger.info("added new comment " + comment);
            commentList.replace(pId, newComments);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090).addService(new CommentService()).build();
        server.start();
        logger.info("Ad Service started, listening on port 9090");
        server.awaitTermination();
    }

}
