package com.patcomp.instagramclient;

import android.app.Application;
import android.app.ApplicationErrorReport;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;

import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import com.squareup.picasso.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import cz.msebera.android.httpclient.client.cache.Resource;

/**
 * Created by patrick on 2/7/16.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private static class ViewHolder {
        TextView Caption;
        ImageView lvPhoto;
        RoundedImageView lvProfilePic;
        TextView tvLikes;
        TextView tvUsername;
        TextView tvTimespan;
        TextView tvAllComments;
        TextView tvComment1;
        TextView tvComment2;
        TextView tvAddComment;
    }

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final InstagramPhoto photo = getItem(position);

        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo_xml, parent, false);

            viewHolder.Caption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.lvPhoto = (ImageView) convertView.findViewById(R.id.IvPhoto);
            viewHolder.lvProfilePic = (RoundedImageView) convertView.findViewById(R.id.IvProfilePic);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTimespan = (TextView) convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.tvAllComments = (TextView) convertView.findViewById(R.id.tvAllComments);
            viewHolder.tvComment1 = (TextView) convertView.findViewById(R.id.tvComment1);
            viewHolder.tvComment2 = (TextView) convertView.findViewById(R.id.tvComment2);
            viewHolder.tvAddComment = (TextView) convertView.findViewById(R.id.tvAddComment);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final VideoView mVideoView = (VideoView) convertView.findViewById(R.id.video_view);
        SpannableString ss = buildStringLinks(photo.username, photo.caption);

        viewHolder.Caption.setText(ss);
        viewHolder.Caption.setMovementMethod(LinkMovementMethod.getInstance());

        String heart = "\u2764";

        DecimalFormat df = new DecimalFormat("#,###");
        String numoflikes =  df.format(photo.likesCount);

        String likeCount = heart + " " +
                numoflikes + " likes" ;


        mVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });



        viewHolder.tvLikes.setText(likeCount);
        viewHolder.tvUsername.setText(photo.username);
        viewHolder.tvTimespan.setText(photo.timespan);

        if (photo.commentsCount > 2)
            viewHolder.tvAllComments.setText("View all " + photo.commentsCount + " comments");
        else
            viewHolder.tvAllComments.setVisibility(View.GONE);

        if (photo.commentsCount >= 1) {
            SpannableString cm1 = buildStringLinks(photo.comments.get(0).username, photo.comments.get(0).text);
            viewHolder.tvComment1.setText(cm1);
            viewHolder.tvComment1.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else
            viewHolder.tvComment1.setVisibility(View.GONE);

        if (photo.commentsCount >= 2) {
            SpannableString cm2 = buildStringLinks(photo.comments.get(1).username, photo.comments.get(1).text);
            viewHolder.tvComment2.setText(cm2);
            viewHolder.tvComment2.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else
            viewHolder.tvComment2.setVisibility(View.GONE);

        viewHolder.tvAddComment.setText("Add a comment");

        viewHolder.lvProfilePic.setImageResource(0);
        viewHolder.lvPhoto.setImageResource(0);

        viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), InstagramUser.class);
                i.putExtra("username", photo.username);
                getContext().startActivity(i);
            }
        });

        final int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());

        viewHolder.lvProfilePic.getLayoutParams().width = 70;
        viewHolder.lvProfilePic.getLayoutParams().height = 70;

        Picasso.with(getContext()).load(photo.profileImageUrl).fit().into(viewHolder.lvProfilePic);

        viewHolder.lvPhoto.getLayoutParams().width = deviceWidth;
        viewHolder.lvPhoto.getLayoutParams().height = (photo.imagHeight/photo.imageWidth)*deviceWidth;

        Picasso.with(getContext()).load(photo.imageUrl).fit().into(viewHolder.lvPhoto);

        return convertView;
    }

    private SpannableString buildStringLinks(final String username, String text) {
        text = username + " " + text;

        SpannableString ss = new SpannableString(text);
        String [] words = text.split(" ");

        for (final String word : words) {
            if (word.startsWith("#") || (word.startsWith("@")) || word.equals(username)){
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (word.startsWith("#")){
                            Intent i = new Intent(getContext(), InstagramHashTag.class);
                            i.putExtra("hashtag", word);
                            getContext().startActivity(i);
                        }
                        else if  (word.startsWith("@") || word.equals(username)){
                            Intent i = new Intent(getContext(), InstagramUser.class);
                            i.putExtra("username", word);
                            getContext().startActivity(i);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };
                int color = ContextCompat.getColor(getContext(), R.color.colorBlueLikes);
                ss.setSpan(clickableSpan, text.indexOf(word), text.indexOf(word)
                        + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(color), text.indexOf(word),
                        text.indexOf(word) + word.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return ss;
    }

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
           // bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

}
