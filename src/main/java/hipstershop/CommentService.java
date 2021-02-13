package hipstershop;

import hipstershop.Demo.Comments;
import hipstershop.Demo.AddCommentRequest;
import hipstershop.Demo.Comment;
import hipstershop.Demo.GetCommentRequest;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CommentService extends hipstershop.CommentServiceGrpc.CommentServiceImplBase {

    private static HashMap<String, ArrayList<Comment>> commentList;

    public CommentService(){
        commentList = new HashMap<>();
        ArrayList<Comment> comments = new ArrayList<>();
        Comment.Builder builder1 = Comment.newBuilder().setProductId("2ZYFJ3GM2N").setUserName("Tom").setCommentText("Liked it!!!").setDate(new Date(System.currentTimeMillis()).toString()).setStars(5);
        comments.add(builder1.build());
        Comment.Builder builder2 = Comment.newBuilder().setProductId("2ZYFJ3GM2N").setUserName("TheOneWhoHates").setCommentText("Hated it!!!").setDate(new Date(System.currentTimeMillis()).toString()).setStars(0);
        comments.add(builder2.build());
        commentList.put("2ZYFJ3GM2N", comments);
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

        if(commentsForId == null) commentsForId = new ArrayList<>();

        return commentsForId;
    }

    private synchronized static void saveComments(String pId, Comment comment) {

        ArrayList<Comment> newComments = commentList.get(pId);

        if(newComments == null) {
            newComments = new ArrayList<>();
            newComments.add(comment);
            commentList.put(pId, newComments);
        } else {
            newComments.add(comment);
            commentList.replace(pId, newComments);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090).addService(new CommentService()).build();
        server.start();
        server.awaitTermination();
    }

}
