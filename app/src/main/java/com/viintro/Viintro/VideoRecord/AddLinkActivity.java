package com.viintro.Viintro.VideoRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thefinestartist.ytpa.enums.Quality;
import com.thefinestartist.ytpa.utils.YouTubeThumbnail;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.Model.Youtube_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.Webservices.YouTubeAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddLinkActivity extends Activity {

    Context context;
    private TextView txt_no_of_words;
    private EditText edtLink, edtDescription;
    private Button btnUpload;
    private ImageView img_thumbnail, imgPlay, img_Close;
    private static String videoId;
    private boolean advertised = false;
    private String link;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_link);
        context = this;
        edtLink = (EditText) findViewById(R.id.edtLink);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        txt_no_of_words = (TextView) findViewById(R.id.txt_no_of_words);
        img_thumbnail  = (ImageView) findViewById(R.id.img_thumbnail);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        img_Close = (ImageView) findViewById(R.id.img_close);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        edtLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!edtLink.getText().toString().equals("")){
                    link = edtLink.getText().toString().replaceAll("\\s+", " ").trim();

                    String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
                    Pattern compiledPattern = Pattern.compile(pattern);
                    Matcher matcher = compiledPattern.matcher(link);
                    if(matcher.find()){
                        //return matcher.group();
                        //Toast.makeText(AddLinkActivity.this, "pattern: " + matcher.group(), Toast.LENGTH_SHORT).show();
                        videoId = matcher.group();

                        Picasso.with(AddLinkActivity.this)
                                .load(YouTubeThumbnail.getUrlFromVideoId(videoId, Quality.HIGH))
                                .fit()
                                .centerCrop()
                                .into(img_thumbnail);

                    } else {
                        // return "error";
                    }
                }else{

                }

                validateFields();
            }


        });

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                description = edtDescription.getText().toString().replaceAll("\\s+", " ").trim();
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                description = edtDescription.getText().toString().replaceAll("\\s+", " ").trim();
                int wordsLength = Validations.countWords(s.toString());// words.length;
                if (wordsLength >= 50) {
                    Validations.setCharLimit(edtDescription, edtDescription.getText().length());
                } else {
                    Validations.removeFilter(edtDescription);
                }
                txt_no_of_words.setText(String.valueOf(wordsLength) + "/" + 50 +" words");
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                validateFields();
            }
        });

        edtLink.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edtLink.clearFocus();
                    edtDescription.requestFocus();

                }
                return handled;
            }
        });

        edtDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edtLink.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtLink.getWindowToken(), 0);
                    handled = true;

                }
                return handled;
            }
        });

        img_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtLink.clearFocus();
                edtDescription.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtLink.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edtDescription.getWindowToken(), 0);
                finish();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                edtLink.clearFocus();
                edtDescription.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtLink.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edtDescription.getWindowToken(), 0);

                link = edtLink.getText().toString().replaceAll("\\s+", " ").trim();
                description = edtDescription.getText().toString().replaceAll("\\s+", " ").trim();
                if(!Validations.checkWordsCount_Caption(description, "description").equals("true"))
                {

                    CommonFunctions.displayToast(context,Validations.checkWordsCount_Caption(description, "description"));
                    return;

                }

                if(CommonFunctions.chkStatus(context)) {

                    if (Patterns.WEB_URL.matcher(link).matches()) {
                        // upload video
                        if (link.contains("youtube")) {

                            CommonFunctions.sDialog(context, "Uploading Video...");
                            YouTubeAPI.req_youtue_upload(context, jsonyoutubeupload(link, description));
                        } else {
                            CommonFunctions.displayToast(context, "Please enter youtube link");
                        }
                    } else {
                        //toast
                        CommonFunctions.displayToast(context, "Please check the link");
                    }
                }
                else
                {
                    CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                }

            }
        });


        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtLink.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtLink.getWindowToken(), 0);

                String link = edtLink.getText().toString().trim();
                if(Patterns.WEB_URL.matcher(link).matches())
                {
                    if(link.contains("youtube")) {

                        Intent intent = new Intent(AddLinkActivity.this, YouTubePlayerActivity.class);
                        // Youtube video ID (Required, You can use YouTubeUrlParser to parse Video Id from url)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, videoId);
                        // Youtube player style (DEFAULT as default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);
                        // Screen Orientation Setting (AUTO for default)
                        // AUTO, AUTO_START_WITH_LANDSCAPE, ONLY_LANDSCAPE, ONLY_PORTRAIT
                        intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);
                        // Show audio interface when user adjust volume (true for default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
                        // If the video is not playable, use Youtube app or Internet Browser to play it
                        // (true for default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
                        // Animation when closing youtubeplayeractivity (none for default)
//                intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_ENTER, R.anim.fade_in);
//                intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_EXIT, R.anim.fade_out);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 1);
                    }
                }
                else {
                    //toast
                    CommonFunctions.displayToast(context,"Please check the link");
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            advertised = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("coming here", " " + videoId);

                }
            }, 1000 * 5);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!advertised)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1000 * 10);
    }

    private void validateFields()
    {
        if (edtLink.getText().toString().replaceAll("\\s+", " ").trim().length() > 0 && edtDescription.getText().toString().replaceAll("\\s+", " ").trim().length() > 0)
        {
            btnUpload.setEnabled(true);
            btnUpload.setBackgroundResource(R.drawable.btn_border_orange);
            btnUpload.setTextColor(Color.parseColor("#ff704c"));
        }
        else
        {
            btnUpload.setEnabled(false);
            btnUpload.setBackgroundResource(R.drawable.btn_border_grey_bg_transparent);
            btnUpload.setTextColor(Color.parseColor("#d6d6d6"));
            btnUpload.setAlpha(0.9f);

        }
    }

    private Youtube_Request jsonyoutubeupload(String url, String desc)
    {
        Video video = new Video();
        video.setType("youtube");
        video.setSource_default(url);
        video.setSource_mpd("");
        video.setSource_hls("");
        video.setThumbnail(YouTubeThumbnail.getUrlFromVideoId(videoId, Quality.HIGH));
        video.setPublic_id("");

        Youtube_Request youtubeRequest = new Youtube_Request();
        youtubeRequest.setClient_id(Constcore.client_Id);
        youtubeRequest.setClient_secret(Constcore.client_Secret);
        youtubeRequest.setDescription(desc);
        youtubeRequest.setVideo(video);

        return youtubeRequest;
    }
}
