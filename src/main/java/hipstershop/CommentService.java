package hipstershop;

import hipstershop.Demo.Comments;
import hipstershop.Demo.Empty;
import hipstershop.Demo.AddCommentRequest;
import hipstershop.Demo.Comment;
import hipstershop.Demo.GetCommentRequest;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CommentService extends CommentServiceGrpc.CommentServiceImplBase {

    private static HashMap<String, ArrayList<Comment>> commentList;

    public CommentService(){
        commentList = new HashMap<String, ArrayList<Comment>>();
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
    public void addComment(AddCommentRequest request, StreamObserver<Empty> observer){
        Comment newComment = request.getComment();
        String pId = newComment.getProductId();

        saveComments(pId, newComment);
    }

    private static List<Comment> getCommentsFromList(String productId) {
        ArrayList<Comment> commentsForId = commentList.get(productId);

        if(commentsForId == null) commentsForId = new ArrayList<Comment>();

        return commentsForId;
    }

    private synchronized static void saveComments(String pId, Comment comment) {

        ArrayList<Comment> newComments = commentList.get(pId);

        if(newComments == null) {
            newComments = new ArrayList<Comment>();
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
