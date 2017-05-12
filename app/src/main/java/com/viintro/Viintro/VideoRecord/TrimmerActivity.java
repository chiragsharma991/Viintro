package com.viintro.Viintro.VideoRecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.lkland.CompressionCompleted;
import com.lkland.videocompressor.compressor.QueuedFFmpegCompressor;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Comment_Video_Request;
import com.viintro.Viintro.Model.Connection_Req_Acc_Rej_Rem;
import com.viintro.Viintro.Model.PostVideo_Request;
import com.viintro.Viintro.Model.ProfileIntro_Video_Request;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.Comment_Video_API;
import com.viintro.Viintro.Webservices.ConnectionRequestAPI;
import com.viintro.Viintro.Webservices.Intro_VideoAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

//import com.lalongooo.videocompressor.video.MediaController;
import com.viintro.Viintro.Webservices.PostVideoAPI;
import com.viintro.Viintro.Webservices.Profile_Video_API;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static com.viintro.Viintro.Constants.Constcore.MAX_NUM_COMMENT;
import static com.viintro.Viintro.Constants.Constcore.cloudinary;
import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.file_cache;
import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.videorecording_activity;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.hDialog_Upload;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.showDialog_Upload;


public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener, CompressionCompleted
{

    private K4LVideoTrimmer mVideoTrimmer;
    private RelativeLayout rel_add_caption;
    private EditText edt_Caption;
    private TextView txt_add_cap_words;
    File file_cache_trim;
    String video_path = "";
    String flag_trim_video = "No";
    Configuration_Parameter m_config;
    Context context;
    //ProgressDialog progressDialog;
    long total_upload_Size = 0;
    private String caption_value;
    private Boolean flag_onResumeCall = false;
    private String errormsg = "";
    File tempFile;
    private String comment_connect;
    private Boolean flag_click_upload = false;
    private String from, from_Activity, from_Camera_Gallery;
    private int user_id, position;// connect
    private String post_id;



    public static void start(Context context, String from, String from_Camera_Gallery, String video_path, String maxDuration, String comment_connect)
    {
        Intent intent = new Intent(context, TrimmerActivity.class);
        intent.putExtra("from",from);
        intent.putExtra("from_Camera_Gallery",from_Camera_Gallery);
        intent.putExtra(Constcore.EXTRA_VIDEO_PATH, video_path);
        intent.putExtra("maxDuration",maxDuration);
        intent.putExtra("comment_connect",comment_connect);
        context.startActivity(intent);

    }


    // When comment is send
    public static void start(Context context, String from, String from_Activity, String from_Camera_Gallery, String video_path, String maxDuration, String post_id, int position, String comment_connect)
    {
        Intent intent = new Intent(context, TrimmerActivity.class);

        intent.putExtra("from",from);
        intent.putExtra("from_Activity",from_Activity);
        intent.putExtra("from_Camera_Gallery",from_Camera_Gallery);
        intent.putExtra(Constcore.EXTRA_VIDEO_PATH, video_path);
        intent.putExtra("maxDuration",maxDuration);
        intent.putExtra("post_id",post_id);
        intent.putExtra("position",position);
        intent.putExtra("comment_connect",comment_connect);
        context.startActivity(intent);

    }

    // at time of sending connection request
    public static void start(Context context, String from, String from_Activity, String from_Camera_Gallery, String video_path, String maxDuration,  int user_id, int position, String comment_connect)
    {
        Intent intent = new Intent(context, TrimmerActivity.class);

        intent.putExtra("from",from);
        intent.putExtra("from_Activity", from_Activity);
        intent.putExtra("from_Camera_Gallery",from_Camera_Gallery);
        intent.putExtra(Constcore.EXTRA_VIDEO_PATH, video_path);
        intent.putExtra("maxDuration",maxDuration);
        intent.putExtra("user_id", user_id);
        intent.putExtra("position",position);
        intent.putExtra("comment_connect",comment_connect);
        context.startActivity(intent);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trimmer);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        file_cache_trim = null;
        flag_trim_video = "No";
        caption_value = "";
        errormsg = "";
        video_path = "";

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Uploading...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setCancelable(false);

        Intent extraIntent = getIntent();
        if (extraIntent != null)
        {
            video_path = extraIntent.getStringExtra(Constcore.EXTRA_VIDEO_PATH);
            comment_connect = extraIntent.getStringExtra("comment_connect");
            from = m_config.from_activity_record_video;
            from_Camera_Gallery = extraIntent.getStringExtra("from_Camera_Gallery");

            if(from.equals("connect") || from.equals("connect_public_profile"))
            {
                from_Activity = getIntent().getExtras().getString("from_Activity");
                user_id = getIntent().getExtras().getInt("user_id");
                position = getIntent().getExtras().getInt("position");
            }
            else if(from.equals("comment"))
            {
                from_Activity = getIntent().getExtras().getString("from_Activity");
                post_id = getIntent().getExtras().getString("post_id");
                position = getIntent().getExtras().getInt("position");
            }


        }



        Log.e("video_path "," "+video_path);
        initialiseUI();

        if (mVideoTrimmer != null) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(video_path);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInmillisec = Long.parseLong(time);
            mVideoTrimmer.setMaxDuration((int) timeInmillisec);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);
            mVideoTrimmer.setVideoURI(Uri.parse(video_path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }

        // call for Compression Related Interface
        QueuedFFmpegCompressor queuedFFmpegCompressor = new QueuedFFmpegCompressor(this);

    }

    private void initialiseUI()
    {
        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        rel_add_caption = (RelativeLayout) findViewById(R.id.rel_add_caption);
        edt_Caption = (EditText) findViewById(R.id.edt_Caption);
        edt_Caption.setTypeface(TextFont.opensans_regular(context));
        txt_add_cap_words = (TextView) findViewById(R.id.txt_add_cap_words);
        txt_add_cap_words.setTypeface(TextFont.opensans_regular(context));

        if(m_config.from_activity_record_video.equals("onboarding") || m_config.from_activity_record_video.equals("edit_intro_video"))
        {
            rel_add_caption.setVisibility(View.GONE);
            edt_Caption.setVisibility(View.GONE);
            txt_add_cap_words.setVisibility(View.GONE);
        }
        else if(m_config.from_activity_record_video.equals("thought"))
        {
            rel_add_caption.setVisibility(View.VISIBLE);
            edt_Caption.setVisibility(View.VISIBLE);
            txt_add_cap_words.setVisibility(View.VISIBLE);
        }
        else if(m_config.from_activity_record_video.equals("comment"))
        {
            rel_add_caption.setVisibility(View.VISIBLE);
            edt_Caption.setVisibility(View.VISIBLE);
            edt_Caption.setHint("Type a Comment");
            edt_Caption.setText(comment_connect);
            edt_Caption.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_NUM_COMMENT) });
            txt_add_cap_words.setVisibility(View.VISIBLE);

        }
        else if(m_config.from_activity_record_video.equals("profile"))
        {
            rel_add_caption.setVisibility(View.GONE);
            edt_Caption.setVisibility(View.GONE);
            txt_add_cap_words.setVisibility(View.GONE);
        }
        else if(m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile"))
        {
            rel_add_caption.setVisibility(View.VISIBLE);
            edt_Caption.setVisibility(View.VISIBLE);
            edt_Caption.setHint("Type a message to connect");
            edt_Caption.setText(comment_connect);
            txt_add_cap_words.setVisibility(View.GONE);


        }

        edt_Caption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_Caption.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_Caption.getWindowToken(), 0);
                    handled = true;

                }
                return handled;
            }
        });

        edt_Caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2)
            {
                if(m_config.from_activity_record_video.equals("thought"))
                {
                    caption_value = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
                }
                else if (m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile") || (m_config.from_activity_record_video.equals("comment")))
                {
                    comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
                }
            }



            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                //caption_value = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
                //if(m_config.from_activity_record_video.equals("thought")) {
                    /*String[] words = s.toString().trim().split("\\s+");
                    int length = words.length;
                    if (s.toString().equals("") || s.toString().equals(" ")) {
                        length = 0;
                    }

                    txt_add_cap_words.setText((50 - length) + " words");
                    Log.e("words.length", " " + length);

                    if (length == 50)
                    {
                        edt_Caption.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edt_Caption.getWindowToken(), 0);
                    } else if (length > 50) {
                        edt_Caption.setFilters(new InputFilter.LengthFilter[0]);

                    }*/

                    int wordsLength = Validations.countWords(s.toString());// words.length;
                    if (wordsLength >= 50) {
                        Validations.setCharLimit(edt_Caption, edt_Caption.getText().length());
                    } else {
                        Validations.removeFilter(edt_Caption);
                    }

                    txt_add_cap_words.setText(String.valueOf(wordsLength) + "/" + 50 +" words");
//                }
//
//                if(m_config.from_activity_record_video.equals("comment"))
//                {
//                    comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
//                    txt_add_cap_words.setText(MAX_NUM_COMMENT - comment_connect.length()  + " characters left");
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onTrimStarted() {

        CommonFunctions.sDialog(context,"Trimming Video...");
    }

    @Override
    public void getResult(final Uri uri) {
        Log.e("getResult uri"," "+uri);
        CommonFunctions.hDialog();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_Caption.getWindowToken(), 0);

        final String trim_videopath = uri.getPath();
        file_cache_trim = new File(trim_videopath);

        if(trim_videopath.equals(video_path))
        {
            flag_trim_video = "No";
        }
        else
        {
            flag_trim_video = "Yes";
        }

        if(!CommonFunctions.chkStatus(context))
        {
            if(flag_trim_video.equals("Yes"))
            {
                createHandler(getResources().getString(R.string.network_connection));
                return;
            }
            return;
        }


        if (m_config.from_activity_record_video.equals("thought"))
        {
            caption_value = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
            if(!Validations.checkWordsCount_Caption(caption_value, m_config.from_activity_record_video).equals("true"))
            {
                if(flag_trim_video.equals("Yes"))
                {
                    createHandler(Validations.checkWordsCount_Caption(caption_value, m_config.from_activity_record_video));
                    return;
                }
                CommonFunctions.displayToast(context,Validations.checkWordsCount_Caption(caption_value, m_config.from_activity_record_video));
                return;
            }
        }
        else if (m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile") || (m_config.from_activity_record_video.equals("comment")))
        {
            comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
            if(!Validations.checkWordsCount_Caption(comment_connect, m_config.from_activity_record_video).equals("true"))
            {
                if(flag_trim_video.equals("Yes"))
                {
                    createHandler(Validations.checkWordsCount_Caption(comment_connect, m_config.from_activity_record_video));
                    return;
                }
                CommonFunctions.displayToast(context,Validations.checkWordsCount_Caption(comment_connect, m_config.from_activity_record_video));
                return;
            }

        }



//        }


        /*if (m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile"))
        {
            comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
            Log.e("comment_connect"," "+comment_connect);
            if(!Validations.checkWordsCount_Connect(comment_connect).equals("true"))
            {
                if(flag_trim_video.equals("Yes"))
                {
                    createHandler(Validations.checkWordsCount_Connect(comment_connect));
                    return;
                }
                CommonFunctions.displayToast(context,Validations.checkWordsCount_Connect(comment_connect));
                return;
            }

        }

        if (m_config.from_activity_record_video.equals("comment"))
        {
            comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
            Log.e("comment_connect"," "+comment_connect);

            if(comment_connect.length() > MAX_NUM_COMMENT)
            {
                if(flag_trim_video.equals("Yes"))
                {
                    createHandler("Comment should not exceed "+MAX_NUM_COMMENT+" characters");
                    return;
                }
                CommonFunctions.displayToast(context,"Comment should not exceed "+MAX_NUM_COMMENT+" characters");
            }

        }*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Log.e("new  path "," "+getString(R.string.video_saved_at, uri.getPath()) +" === "+trim_videopath+"====== "+file_cache_trim);
                Log.e("trim_videopath ", " " + trim_videopath + " file_cache_trim " + file_cache_trim);
                Log.e("file_cache ", " " + file_cache);
                Log.e("equals ", " " + trim_videopath.equals(video_path));
                Log.e("flag_trim_video", " " + flag_trim_video);

                if (getIntent().getExtras().getString("from_Camera_Gallery").equals("fromGallery"))
                {

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(trim_videopath);
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInmillisec = Long.parseLong(time);
                    int height = Integer.parseInt(retriever.extractMetadata(retriever.METADATA_KEY_VIDEO_HEIGHT));
                    int width = Integer.parseInt(retriever.extractMetadata(retriever.METADATA_KEY_VIDEO_WIDTH));

                    Log.e("duration", " " + timeInmillisec);

                    if (m_config.from_activity_record_video.equals("onboarding") || m_config.from_activity_record_video.equals("edit_intro_video"))
                    {
                        if (timeInmillisec > Constcore.record_time_onboarding)
                        {
                            CommonFunctions.displayToast(context, "Video duration should not exceed 120 sec");
                            return;
                        }
                    }

                    if (m_config.from_activity_record_video.equals("profile"))
                    {
                        if (timeInmillisec > Constcore.record_time_thought)
                        {
                            CommonFunctions.displayToast(context, "Video duration should not exceed 60 sec");
                            return;
                        }
                    }

                    if (m_config.from_activity_record_video.equals("comment"))
                    {
                        if (timeInmillisec > Constcore.record_time_comment)
                        {
                            CommonFunctions.displayToast(context, "Video duration should not exceed 30 sec");
                            return;
                        }
                    }


                    if (m_config.from_activity_record_video.equals("thought"))
                    {
                        if (timeInmillisec > Constcore.record_time_thought)
                        {
                            CommonFunctions.displayToast(context, "Video duration should not exceed 30 sec");
                            return;
                        }
                    }

                    if (m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile"))
                    {
                        if (timeInmillisec > Constcore.record_time_connect)
                        {
                            CommonFunctions.displayToast(context, "Video duration should not exceed 30 sec");
                            return;
                        }
                    }

                    if (width > height) {
                        // landscape
                        if (width == 640 && height == 480)
                        {
                            //no compression
                            if (trim_videopath.equals(video_path))
                            {
                                // no trim
                                //CommonFunctions.sDialog(context,"Uploading Video...");
                                new uploadvideo_cloud_foregrnd(file_cache_trim.getPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            else
                            {
                                //trim
                                CommonFunctions.sDialog(context,"Saving Video...");
                                SaveVideoinSDCard();
                            }
                        }
                        else
                        {
                            //compress
                            //CommonFunctions.sDialog(context, "Uploading Video...");
                            /*Video-Compressor-master
                             tempFile = file_cache_trim;
                             new VideoCompressor().execute();
                             */
                            /** Without Compression**/
                             new uploadvideo_cloud_foregrnd(file_cache_trim.getPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                             /**/

                           /***
                            CommonFunctions.sDialog(context,"Compressing Video...");
                            Intent intent = new Intent(TrimmerActivity.this, CompressionService.class);
                            intent.putExtra(CompressionService.TAG_ACTION,CompressionService.FLAG_ACTION_ADD_VIDEO);
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_PATH, createFolderInExternalStorageDirectory("Viintro"));
                            intent.putExtra(CompressionService.TAG_DATA_INPUT_FILE_PATH, file_cache_trim.getPath());
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_NAME,file_cache_trim.getName().toString());
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_SIZE,"");
                            intent.putExtra("flag_trim_video",flag_trim_video);
                            Log.e("intent"," === "+intent);
                            context.startService(intent);
                            ***/


                        }
                    } else
                    {

                        //portrait
                        if (width == 480 && height == 640)
                        {
                            //no compression
                            if (trim_videopath.equals(video_path))
                            {
                                // no trim
                                new uploadvideo_cloud_foregrnd(file_cache_trim.getPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            else
                            {
                                //trim
                                CommonFunctions.sDialog(context,"Saving Video...");
                                SaveVideoinSDCard();
                            }

                        }
                        else
                        {
                            //compress
                            //CommonFunctions.sDialog(context, "Uploading Video...");
                            /**Video-Compressor-master
                             tempFile = file_cache_trim;
                             new VideoCompressor().execute();
                             **/
                            /** Without Compression**/
                            new uploadvideo_cloud_foregrnd(file_cache_trim.getPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                             /**/

                            /***
                            CommonFunctions.sDialog(context,"Compressing Video...");
                            Intent intent = new Intent(TrimmerActivity.this, CompressionService.class);
                            intent.putExtra(CompressionService.TAG_ACTION,CompressionService.FLAG_ACTION_ADD_VIDEO);
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_PATH, createFolderInExternalStorageDirectory("Viintro"));
                            intent.putExtra(CompressionService.TAG_DATA_INPUT_FILE_PATH, file_cache_trim.getPath());
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_NAME,file_cache_trim.getName().toString());
                            intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_SIZE,"");
                            intent.putExtra("flag_trim_video",flag_trim_video);
                            Log.e("intent"," === "+intent);
                            context.startService(intent);
                            /***/

                        }
                    }
                }
                else
                {
                    //from video record
                    //compress
                    //CommonFunctions.sDialog(context, "Uploading Video...");
                    /**Video-Compressor-master
                     tempFile = file_cache_trim;
                     new VideoCompressor().execute();
                     **/
                    /** Without Compression*/
                    CommonFunctions.sDialog(context,"Saving Video...");
                    SaveVideoinSDCard();
                     /**/

                    /***
                    CommonFunctions.sDialog(context,"Compressing Video...");
                    Intent intent = new Intent(TrimmerActivity.this, CompressionService.class);
                    intent.putExtra(CompressionService.TAG_ACTION,CompressionService.FLAG_ACTION_ADD_VIDEO);
                    intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_PATH, createFolderInExternalStorageDirectory("Viintro"));
                    intent.putExtra(CompressionService.TAG_DATA_INPUT_FILE_PATH, file_cache_trim.getPath());
                    intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_NAME,file_cache_trim.getName().toString());
                    intent.putExtra(CompressionService.TAG_DATA_OUTPUT_FILE_SIZE,"");
                    intent.putExtra("flag_trim_video",flag_trim_video);
                    Log.e("intent"," === "+intent);
                    context.startService(intent);
                    /***/
                }
            }
        });

    }

    private String createFolderInExternalStorageDirectory(String folderName) {

        String path;
        File file;

        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(isSDPresent)
        {
            // yes SD-card is present
            Log.e(" yes SD-card is present",""+ isSDPresent);
            file = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM +"/"+folderName);
            path = file.getPath();
        }
        else
        {
            file  = new File(Environment.getExternalStorageDirectory(),"/"+folderName);
            path = file.getPath();
        }

        if (!file.exists())
        {
            file.mkdirs();
        }

        return path;
    }

    @Override
    public void cancelAction()
    {

        CommonFunctions.hDialog();
        mVideoTrimmer.destroy();

        if(file_cache != null)
        {
            file_cache.delete();
        }

        Log.e("video_path ="," "+video_path);
        Log.e("file_cache_trim ="," "+file_cache_trim);

        if(file_cache_trim != null)
        {
//            if(video_path == null)
//            {
//                file_cache_trim.delete();
//            }
//            else
//            {
            if(!flag_trim_video.equals("No"))
            {
                file_cache_trim.delete();
            }

            //}

        }
        finish();
        videorecording_activity.finish();


        if(from.equals("connect") || from.equals("connect_public_profile"))
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from, from_Activity, user_id, position);
        }
        else if(from.equals("connect"))
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from, from_Activity, post_id, position);
        }
        else
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from);
        }


    }

    @Override
    public void onError(final String message) {
        CommonFunctions.hDialog();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        CommonFunctions.hDialog();
        mVideoTrimmer.destroy();
        if (file_cache != null) {
            file_cache.delete();
        }
        if (file_cache_trim != null) {
            if (video_path == null) {
                file_cache_trim.delete();
            } else {
                if (!flag_trim_video.equals("No")) {
                    file_cache_trim.delete();
                }

            }
        }
        finish();
        videorecording_activity.finish();

        if(from.equals("connect") || from.equals("connect_public_profile"))
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from, from_Activity, user_id, position);
        }
        else if(from.equals("comment"))
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from, from_Activity, post_id, position);
        }
        else
        {
            VideoRecordingActivity.start(TrimmerActivity.this, from);
        }
    }

    private void SaveVideoinSDCard() {

        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        final File sdfile;

        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if(isSDPresent)
        {
            // yes SD-card is present
            Log.e(" yes SD-card is present",""+ isSDPresent);
            sdfile = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM +"/Viintro");

        }
        else
        {
            // Sorry
            Log.e(" no SD-card is present","");
            sdfile  = new File(Environment.getExternalStorageDirectory(),"/"+"Viintro");

        }

        final File filetosd;

        if(file_cache_trim == null)
        {
            filetosd = new File(sdfile, file_cache.getName());
        }
        else
        {
            filetosd = new File(sdfile, file_cache_trim.getName());
        }

        try {
            // Make sure the Pictures directory exists.
            sdfile.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is;

            if(file_cache_trim == null)
            {
                is = new FileInputStream(file_cache);
            }
            else
            {
                is = new FileInputStream(file_cache_trim);
            }


            OutputStream os = new FileOutputStream(filetosd);
            byte[] data = new byte[is.available()];
            Log.e("isavailable "," "+is.available());
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { filetosd.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);

                            if(file_cache != null)
                                file_cache.delete();
                            if(file_cache_trim != null)
                                file_cache_trim.delete();

                            // this variable is set because when i stop activity after video saved to sd card it gets crashed
                            video_path = path;
                            new uploadvideo_cloud_foregrnd(path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        }
                    });

        } catch (Exception e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file_cache, e);
            CommonFunctions.hDialog();
        }

    }

    @Override
    public void onVideoPrepared() {

    }


    public void createHandler(final String msg)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run()
            {
                CommonFunctions.displayToast(context, msg.toString());
            }
        });
    }

    @Override
    public void oncompressioncompleted(Boolean val) {

        if(val == true)
        {

            File filetosd;

            if(file_cache_trim == null)
            {
                filetosd = new File(createFolderInExternalStorageDirectory("Viintro"), file_cache.getName());
            }
            else
            {
                filetosd = new File(createFolderInExternalStorageDirectory("Viintro"), file_cache_trim.getName());
            }



            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { filetosd.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);

                            if(file_cache != null)
                                file_cache.delete();
                            if(file_cache_trim != null)
                            {
                                if(!flag_trim_video.equals("No"))
                                {
                                    file_cache_trim.delete();
                                }
                            }

                            // this variable is set because when i stop activity after video saved to sd card it gets crashed
                            video_path = path;

                            new uploadvideo_cloud_foregrnd(path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                        }
                    });

        }
    }

    //class to compress video
    /*class VideoCompressor extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("","Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.e("tempfile"," "+tempFile);
            try
            {
                return MediaController.getInstance().convertVideo(tempFile.getPath());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errormsg = "Some error occured";
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if(compressed)
            {
                Log.d("","Compression successfully!");
                CommonFunctions.hDialog();

                File filetosd;

                if(file_cache_trim == null)
                {
                    filetosd = new File(createFolderInExternalStorageDirectory("Viintro"), file_cache.getName());
                }
                else
                {
                    filetosd = new File(createFolderInExternalStorageDirectory("Viintro"), file_cache_trim.getName());
                }

                Log.e("filetosd"," "+filetosd);

                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(context,
                        new String[] { filetosd.toString() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);

                                if(file_cache != null)
                                    file_cache.delete();
                                if(file_cache_trim != null)
                                {
                                    if(!flag_trim_video.equals("No"))
                                    {
                                        file_cache_trim.delete();
                                    }
                                }

                            }
                        });


                CommonFunctions.hDialog();
                new MultiPartrequestVolley(filetosd, context).execute();

            }
            else
            {
                CommonFunctions.hDialog();
                Toast.makeText(getApplicationContext()," "+errormsg,Toast.LENGTH_SHORT).show();

            }
        }
    }*/

    /**
     * Uploading the file to server
     * */

    public static Uri getImageContentUri(Context context, File videofile) {
        String filePath = videofile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID },
                MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (videofile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag_onResumeCall == true)
        {
            if(flag_click_upload == true)
            {

                if(m_config.from_activity_record_video.equals("intro"))
                {
                    ViintroApplication.getInstance().cancelPendingRequests("intro_video_api");
                }
                else if(m_config.from_activity_record_video.equals("thought"))
                {
                    ViintroApplication.getInstance().cancelPendingRequests("thought_video_api");
                }
                else if(m_config.from_activity_record_video.equals("comment"))
                {
                    ViintroApplication.getInstance().cancelPendingRequests("comment_video_api");
                }
                else if(m_config.from_activity_record_video.equals("profile"))
                {
                    ViintroApplication.getInstance().cancelPendingRequests("profile_video_api");
                }
                else if(m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile"))
                {
                    ViintroApplication.getInstance().cancelPendingRequests("connect_video_api");
                }
                finish();
                flag_onResumeCall = false;
                return;

            }

            String maxDur = getIntent().getExtras().getString("maxDuration");
            String comment_connect = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();//getIntent().getExtras().getString("comment_connect");
            if(from.equals("connect") || from.equals("connect_public_profile"))
            {
//
                String from_Activity = getIntent().getExtras().getString("from_Activity");

                TrimmerActivity.start(context, from, from_Activity, from_Camera_Gallery, video_path, maxDur, user_id, position, comment_connect);
            }
            else if(from.equals("comment"))
            {
                TrimmerActivity.start(context, from, from_Activity, from_Camera_Gallery, video_path, maxDur, post_id, position, comment_connect);
            }
            else
            {
                TrimmerActivity.start(context, from, from_Camera_Gallery, video_path, maxDur, comment_connect);
            }


            finish();
            flag_onResumeCall = false;
        }
        flag_onResumeCall = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog();
    }

    public class uploadvideo_cloud_foregrnd extends AsyncTask<Integer, Integer, Map> {

        String sdpath;
        Map result;

        public uploadvideo_cloud_foregrnd(String path)
        {
            flag_click_upload = true;
            sdpath = path;
        }



        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            CommonFunctions.hDialog();
            new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                @Override
                public void run()
                {
                    LayoutInflater inflater = getLayoutInflater();
                    edt_Caption.setEnabled(false);
                    if(showDialog_Upload(context) != null)
                    {

                        showDialog_Upload(context);
                        showDialog_Upload(context).setProgress(0);
                    }
                }
            });

        }

        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            Log.e("progress[0]"," "+progress[0]);
            if(progress[0] < 95 && showDialog_Upload(context) != null)
            {
                showDialog_Upload(context).setProgress(progress[0]);
            }
        }

        protected void onCancelled() {
            if(showDialog_Upload(context) != null)
            {
                showDialog_Upload(context).setMax(0); // stop the progress
            }

        }

        @Override
        protected Map doInBackground(Integer... params)
        {
            try
            {
                File file = new File(sdpath);
                final long size = (int) file.length()/100;
                Log.e("size "," "+size +" ");

                for (long i = 0; i <= size; i++)
                {
                    try
                    {
                        publishProgress((int) ((i / (float)size) * 100));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                //Log.e("selectedImagePath "," "+sdpath +" ");
                result = cloudinary.uploader().upload(sdpath,
                        ObjectUtils.asMap("resource_type", "video",
                                "eager", Arrays.asList(
                                        new Transformation().fetchFormat("mpd"),
                                        new Transformation().fetchFormat("m3u8"),
                                        new Transformation().fetchFormat("mp4")),
                                "eager_async", true));



                Log.e("Map"," "+result.toString() +" "+size);

            }
            catch (final Exception e)
            {
                e.printStackTrace();
                e.getMessage();
                Handler h = new Handler(context.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        hDialog_Upload();
                        ((Activity)context).finish();
                        if(videorecording_activity != null)
                        {
                            videorecording_activity.finish();
                        }
                    }
                });
            }

            return result;
        }

        @Override
        protected void onPostExecute(Map result) {

            if(result == null)
            {
                Toast.makeText(context,"Video upload failed",Toast.LENGTH_SHORT).show();
                hDialog_Upload();
                ((Activity)context).finish();
                if(videorecording_activity != null)
                {
                    videorecording_activity.finish();
                }

            }
            else
            {


                JSONObject json_res = null;
                String public_id = "",createdAt = "";
                JSONArray eager;
                JSONObject obj_mpd,obj_m3u8,obj_mp4;
                String secureurl_mpd = "",secureurl_m3u8 = "",secureurl_mp4 = "";
                String thumbnail = "";

                try
                {
                    json_res = new JSONObject(result);
                    public_id = json_res.getString("public_id");
                    createdAt = json_res.getString("created_at");
                    eager = json_res.getJSONArray("eager");
                    obj_mpd = eager.getJSONObject(0);
                    obj_m3u8 = eager.getJSONObject(1);
                    obj_mp4 = eager.getJSONObject(2);
                    secureurl_mpd = obj_mpd.getString("secure_url");
                    secureurl_m3u8 = obj_m3u8.getString("secure_url");
                    secureurl_mp4 = obj_mp4.getString("secure_url");
                    thumbnail = "http://res.cloudinary.com/"+Constcore.cloud_name+"/video/upload/"+public_id+".jpg";

                    Log.e("public_id1"," "+public_id);
                    Log.e("createdAt"," "+createdAt);
                    Log.e("secureurl_mpd"," "+secureurl_mpd);
                    Log.e("secureurl_m3u8"," "+secureurl_m3u8);
                    Log.e("secureurl_mp4"," "+secureurl_mp4);
                    Log.e("thumbnail"," "+thumbnail);

                    if(m_config.from_activity_record_video.equals("onboarding") || m_config.from_activity_record_video.equals("edit_intro_video"))
                    {
                        ProfileIntro_Video_Request profileVideo_request = json_profilevideo_req(public_id, createdAt, secureurl_mpd, secureurl_m3u8, secureurl_mp4, thumbnail);
                        Intro_VideoAPI.req_intro_video_API(context, profileVideo_request);
                    }
                    else if(m_config.from_activity_record_video.equals("thought"))
                    {
                        PostVideo_Request postVideo_request = json_post_video_upload(public_id, secureurl_mpd, secureurl_m3u8, secureurl_mp4, thumbnail, caption_value);
                        PostVideoAPI.req_post_video_api(context, postVideo_request);
                    }
                    else if(m_config.from_activity_record_video.equals("profile"))
                    {
                        ProfileIntro_Video_Request profileVideo_request = json_profilevideo_req(public_id, createdAt, secureurl_mpd, secureurl_m3u8, secureurl_mp4, thumbnail);
                        Profile_Video_API.req_profilevideo_API(context, profileVideo_request);
                    }
                    else if(m_config.from_activity_record_video.equals("connect") || m_config.from_activity_record_video.equals("connect_public_profile"))
                    {
                        int position = getIntent().getExtras().getInt("position");
                        Connection_Req_Acc_Rej_Rem connect_video_request = json_connect_request(public_id, createdAt, secureurl_mpd, secureurl_m3u8, secureurl_mp4, thumbnail);
                        Log.e("from ==", " "+getIntent().getExtras().getString("from_Activity"));
                        ConnectionRequestAPI connectionRequestAPI = new ConnectionRequestAPI();
                        connectionRequestAPI.req_connection_API(context, connect_video_request, position, from_Activity);

                    }
                    else if(m_config.from_activity_record_video.equals("comment"))
                    {

                        Comment_Video_Request comment_video_request = json_comment_video_request(public_id, createdAt, secureurl_mpd, secureurl_m3u8, secureurl_mp4, thumbnail);
                        Comment_Video_API comment_video_api = new Comment_Video_API();
                        comment_video_api.req_commentvideo_API(context, comment_video_request, post_id, from_Activity);
                    }



                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(context,"Video upload failed",Toast.LENGTH_SHORT).show();
                    hDialog_Upload();
                    ((Activity)context).finish();
                    if(videorecording_activity != null)
                    {
                        videorecording_activity.finish();
                    }
                }
            }
        }
    }

    public ProfileIntro_Video_Request json_profilevideo_req(String publicid, String createdat, String secureurl_mpd, String secureurl_m3u8, String secureurl_mp4, String thumbnail) {
        ProfileIntro_Video_Request profileVideo_request = new ProfileIntro_Video_Request();
        profileVideo_request.setClient_id(Constcore.client_Id);
        profileVideo_request.setClient_secret(Constcore.client_Secret);
        profileVideo_request.setPublic_id(publicid);
        profileVideo_request.setCreated_at(createdat);
        profileVideo_request.setSecure_url_mpd(secureurl_mpd);
        profileVideo_request.setSecure_url_hls(secureurl_m3u8);
        profileVideo_request.setSecure_url_mp4(secureurl_mp4);
        profileVideo_request.setThumbnail(thumbnail);

        return profileVideo_request;
    }

    private PostVideo_Request json_post_video_upload(String public_id, String secureurl_mpd, String secureurl_m3u8, String secureurl_mp4, String thumbnail, String caption_value)
    {
        Video video = new Video();
        video.setType("system");
        video.setSource_default(secureurl_mp4);
        video.setSource_mpd(secureurl_mpd);
        video.setSource_hls(secureurl_m3u8);
        video.setThumbnail(thumbnail);
        video.setPublic_id(public_id);

        PostVideo_Request postVideo_request = new PostVideo_Request();
        postVideo_request.setClient_id(Constcore.client_Id);
        postVideo_request.setClient_secret(Constcore.client_Secret);
        postVideo_request.setDescription(caption_value);
        postVideo_request.setVideo(video);

        return postVideo_request;
    }

    private Connection_Req_Acc_Rej_Rem json_connect_request(String public_id, String createdAt, String secureurl_mpd, String secureurl_m3u8, String secureurl_mp4, String thumbnail)
    {
        int user_id = getIntent().getExtras().getInt("user_id");
        String message_comment_connect = comment_connect;

        //API call for connect
        //CommonFunctions.displayToast(context, "API call");
        Connection_Req_Acc_Rej_Rem connection_request = new Connection_Req_Acc_Rej_Rem();
        connection_request.setClient_id(Constcore.client_Id);
        connection_request.setClient_secret(Constcore.client_Secret);
        connection_request.setUser_id(user_id);
        connection_request.setMessage(message_comment_connect);
        connection_request.setPublic_id(public_id);
        connection_request.setCreated_at(createdAt);
        connection_request.setSecure_url_mpd(secureurl_mpd);
        connection_request.setSecure_url_hls(secureurl_m3u8);
        connection_request.setSecure_url_mp4(secureurl_mp4);
        connection_request.setThumbnail(thumbnail);

        return connection_request;
    }

    private Comment_Video_Request json_comment_video_request(String public_id, String createdAt, String secureurl_mpd, String secureurl_m3u8, String secureurl_mp4, String thumbnail)
    {

        String message_comment_connect = comment_connect;

        Video video = new Video();
        video.setType("system");
        video.setSource_default(secureurl_mp4);
        video.setSource_mpd(secureurl_mpd);
        video.setSource_hls(secureurl_m3u8);
        video.setThumbnail(thumbnail);
        video.setPublic_id(public_id);


        Comment_Video_Request comment_video_request = new Comment_Video_Request();
        comment_video_request.setClient_id(Constcore.client_Id);
        comment_video_request.setClient_secret(Constcore.client_Secret);
        comment_video_request.setComment_data(message_comment_connect);
        comment_video_request.setVideo(video);

        return comment_video_request;
    }



}