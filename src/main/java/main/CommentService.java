package main;

import hipstershop.CommentServiceGrpc;
import hipstershop.Demo.Comment;
import hipstershop.Demo.GetCommentRequest;
import io.grpc.stub.StreamObserver;

public class CommentService extends CommentServiceGrpc.CommentServiceImplBase {

    @Override
    public void getComment(GetCommentRequest request, StreamObserver<Comment> comment){
        String pId = request.getProductId();

        Comment.Builder builder = Comment.newBuilder();

        builder.setCommentString(
                "Comments for " + pId
        );

        comment.onNext(builder.build());
        comment.onCompleted();
    }
}
