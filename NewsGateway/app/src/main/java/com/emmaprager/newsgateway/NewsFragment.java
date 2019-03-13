package com.emmaprager.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private static final String ARTICLE= "ARTICLE";
    private static final String COUNT= "COUNT";

    public static NewsFragment newInstance(Article a, String c) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARTICLE, a);
        args.putString(COUNT, c);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Article a = (Article) getArguments().getSerializable(ARTICLE);
        String c = getArguments().getString(COUNT);
        View v = inflater.inflate(R.layout.news_fragment, container, false);
        TextView headline = v.findViewById(R.id.Headline);
        TextView date = v.findViewById(R.id.Date);
        TextView author = v.findViewById(R.id.Author);
        TextView description = v.findViewById(R.id.Text);
        TextView count = v.findViewById(R.id.Count);
        final ImageView image = v.findViewById(R.id.Image);

        count.setText(c);

        if(a.getTitle()!=null && !a.getTitle().equals("null")){
            headline.setText(a.getTitle());
            headline.setMovementMethod(LinkMovementMethod.getInstance());
            headline.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(a.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
        if(a.getAuthor()!=null && !a.getAuthor().equals("null")){
            author.setText(a.getAuthor());
            author.setMovementMethod(LinkMovementMethod.getInstance());
            author.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(a.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
        if(a.getPublishedAt()!=null && !a.getPublishedAt().equals("null")){
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date now = readFormat.parse(a.getPublishedAt());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm aa", Locale.US);
                date.setText(sdf.format(now));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(a.getDescription()!=null && !a.getDescription().equals("null")){
            description.setText(a.getDescription());
            description.setMovementMethod(LinkMovementMethod.getInstance());
            description.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(a.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
        if(a.getUrlToImage()!= null && !a.getUrlToImage().equals("null")){
            Picasso picasso = new Picasso.Builder(getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = a.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.broken)
                            .placeholder(R.drawable.loading)
                            .into(image);
                }
            }).build();
            picasso.load(a.getUrlToImage())
                    .fit()
                    .centerInside()
                    .error(R.drawable.broken)
                    .placeholder(R.drawable.loading)
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(a.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
        return v;
    }
}