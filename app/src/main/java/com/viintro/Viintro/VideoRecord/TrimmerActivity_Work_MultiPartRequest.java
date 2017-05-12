package com.viintro.Viintro.VideoRecord;

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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.lalongooo.videocompressor.video.MediaController;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.PostVideo_Request;
import com.viintro.Viintro.Model.ProfileIntro_Video_Request;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.Webservices.PostVideoAPI;
import com.viintro.Viintro.Webservices.Intro_VideoAPI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.file_cache;


public class TrimmerActivity_Work_MultiPartRequest extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener
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
    ProgressDialog progressDialog;
    long total_upload_Size = 0;
    String caption_value;
    private Boolean flag_onResumeCall = false;
    private String errormsg = "";
    File tempFile;
    private String comment_connect;

    public static void start(Context context, String video_path, String maxDuration, String from)
    {
        Intent intent = new Intent(context, TrimmerActivity_Work_MultiPartRequest.class);
        intent.putExtra(Constcore.EXTRA_VIDEO_PATH, video_path);
        intent.putExtra("maxDuration",maxDuration);
        intent.putExtra("from",from);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_trimmer);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        file_cache_trim = null;
        flag_trim_video = "No";
        caption_value = "";
        errormsg = "";

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        Intent extraIntent = getIntent();

        video_path = "";

        if (extraIntent != null)
        {
            video_path = extraIntent.getStringExtra(Constcore.EXTRA_VIDEO_PATH);
            comment_connect = extraIntent.getStringExtra("comment_connect");
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


    }

    private void initialiseUI()
    {
        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        rel_add_caption = (RelativeLayout) findViewById(R.id.rel_add_caption);
        edt_Caption = (EditText) findViewById(R.id.edt_Caption);
        txt_add_cap_words = (TextView) findViewById(R.id.txt_add_cap_words);

        if(m_config.from_activity_record_video.equals("onboarding"))
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
            rel_add_caption.setVisibility(View.GONE);
            edt_Caption.setVisibility(View.GONE);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                caption_value = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
                String[] words = caption_value.trim().split("\\s+");
                txt_add_cap_words.setText((50 - words.length) + " words");
                if(words.length == 50)
                {
                    edt_Caption.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_Caption.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public void onTrimStarted() {
        //mProgressDialog.setMessage("Trimming...");
        //mProgressDialog.show();
        CommonFunctions.sDialog(context,"Trimming Video...");
    }

    @Override
    public void getResult(final Uri uri) {
        //mProgressDialog.cancel();
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


//        if (m_config.from_activity_record_video.equals("thought"))
//        {
//            caption_value = edt_Caption.getText().toString().replaceAll("\\s+", " ").trim();
//            if(!Validations.checkWordsCount_Caption(caption_value, ).equals("true"))
//            {
//                if(flag_trim_video.equals("Yes"))
//                {
//                    createHandler(Validations.checkWordsCount_Caption(caption_value));
//                    return;
//                }
//                CommonFunctions.displayToast(context,Validations.checkWordsCount_Caption(caption_value));
//                return;
//            }
//
//        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Log.e("new  path "," "+getString(R.string.video_saved_at, uri.getPath()) +" === "+trim_videopath+"====== "+file_cache_trim);
                Log.e("trim_videopath ", " " + trim_videopath + " file_cache_trim " + file_cache_trim);
                Log.e("file_cache ", " " + file_cache);
                Log.e("equals ", " " + trim_videopath.equals(video_path));
                Log.e("flag_trim_video", " " + flag_trim_video);

                if (getIntent().getExtras().getString("from").equals("fromGallery")) {

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(trim_videopath);
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInmillisec = Long.parseLong(time);
                    int height = Integer.parseInt(retriever.extractMetadata(retriever.METADATA_KEY_VIDEO_HEIGHT));
                    int width = Integer.parseInt(retriever.extractMetadata(retriever.METADATA_KEY_VIDEO_WIDTH));

                    Log.e("duration", " " + timeInmillisec);

                    if (m_config.from_activity_record_video.equals("onboarding"))
                    {
                        if (timeInmillisec > Constcore.record_time_onboarding)
                        {
                            Toast.makeText(TrimmerActivity_Work_MultiPartRequest.this, "Video duration should not exceed 120 sec", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (m_config.from_activity_record_video.equals("thought"))
                    {
                        if (timeInmillisec > Constcore.record_time_thought)
                        {
                            Toast.makeText(TrimmerActivity_Work_MultiPartRequest.this, "Video duration should not exceed 60 sec", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (m_config.from_activity_record_video.equals("comment"))
                    {
                        if (timeInmillisec > Constcore.record_time_comment)
                        {
                            Toast.makeText(TrimmerActivity_Work_MultiPartRequest.this, "Video duration should not exceed 30 sec", Toast.LENGTH_SHORT).show();
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
                                Log.e("request time", " " + System.currentTimeMillis());
                                new MultiPartrequestVolley(file_cache_trim, context).execute();
                            }
                            else
                            {
                                //trim
                                //CommonFunctions.sDialog(context,"Uploading Video...");
                                SaveVideoinSDCard();
                            }
                        }
                        else
                        {
                            //compress
                            CommonFunctions.sDialog(context, "Compressing Video...");
                            tempFile = file_cache_trim;
                            //new VideoCompressor().execute();
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
                                // CommonFunctions.sDialog(context,"Uploading Video...");
                                Log.e("request time", " " + System.currentTimeMillis());
                                new MultiPartrequestVolley(file_cache_trim, context).execute();
                            }
                            else
                            {
                                //trim
                                //CommonFunctions.sDialog(context,"Uploading Video...");
                                SaveVideoinSDCard();
                            }

                        }
                        else
                        {
                            //compress
                            CommonFunctions.sDialog(context, "Compressing Video...");

                            tempFile = file_cache_trim;
                            //new VideoCompressor().execute();

                        }
                    }
                }
                else
                {
                    //from video record
                    //compress
                    CommonFunctions.sDialog(context, "Compressing Video...");
//                    Uri uri1 = getImageContentUri(context,file_cache_trim);
//                    Log.e("uri1"," "+uri1);
//                    if (uri1 != null)
//                    {
//                        String filePath = FileUtils.getPath(context, uri1);
//                        tempFile = new File(filePath);
//                        Log.e("filePath ", " " + filePath);
//                        new VideoCompressor().execute();
//
//                    }
//                    else
//                    {
//
//                    }
                    tempFile = file_cache_trim;
                    //new VideoCompressor().execute();
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

            //file  = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM),"/"+folderName);
            file  = new File(Environment.getExternalStorageDirectory(),"/"+folderName);
            path = file.getPath();


        }

        if (!file.exists()) {
            boolean ret = file.mkdirs();
        }

        return path;
    }

    @Override
    public void cancelAction()
    {
        //mProgressDialog.cancel();
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
        //VideoRecordingActivity.start(TrimmerActivity_Work_MultiPartRequest.this, from);
//        Intent i = new Intent(TrimmerActivity.this, InternalShareActivity.class);
//        startActivity(i);
    }

    @Override
    public void onError(final String message) {
        //mProgressDialog.cancel();
        CommonFunctions.hDialog();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity_Work_MultiPartRequest.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //mProgressDialog.cancel();
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
        //VideoRecordingActivity.start(TrimmerActivity_Work_MultiPartRequest.this, from);
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
            //sdfile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "/Viintro");
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

                            new MultiPartrequestVolley(filetosd, context).execute();

                        }
                    });

        } catch (Exception e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file_cache, e);
            //mProgressDialog.hide();
            CommonFunctions.hDialog();
        }

    }

    public ProfileIntro_Video_Request json_profilevideo_req(String publicid, String createdat, String url) {
        ProfileIntro_Video_Request profileVideo_request = new ProfileIntro_Video_Request();
        profileVideo_request.setClient_id(Constcore.client_Id);
        profileVideo_request.setClient_secret(Constcore.client_Secret);
        profileVideo_request.setPublic_id(publicid);
        profileVideo_request.setCreated_at(createdat);
        profileVideo_request.setSecure_url_mpd(url);
        profileVideo_request.setSecure_url_hls(url);
        profileVideo_request.setSecure_url_mp4(url);

        return profileVideo_request;
    }

    @Override
    public void onVideoPrepared() {

    }

    private PostVideo_Request json_post_video_upload(String public_id, String url, String caption_value)
    {
        Video video = new Video();
        video.setType("system");
//        video.setSource(url);
//        video.setPublic_id(public_id);
        video.setThumbnail("");

        PostVideo_Request postVideo_request = new PostVideo_Request();
        postVideo_request.setClient_id(Constcore.client_Id);
        postVideo_request.setClient_secret(Constcore.client_Secret);
        postVideo_request.setDescription(caption_value);
        postVideo_request.setVideo_source("system");
        postVideo_request.setVideo(video);

        return postVideo_request;
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
    protected void onResume() {
        super.onResume();
        if(flag_onResumeCall == true)
        {
            finish();
            TrimmerActivity_Work_MultiPartRequest.start(context, video_path, getIntent().getExtras().getString("maxDuration"),getIntent().getExtras().getString("from"));
            flag_onResumeCall = false;
        }
        flag_onResumeCall = true;

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
    private class MultiPartrequestVolley extends AsyncTask<Void, Integer, String> {

        File file;
        Context context;
        String url = "https://api.cloudinary.com/v1_1/"+Constcore.cloud_name+"/video/upload";

        public MultiPartrequestVolley(File file, final Context context) {

            this.file = file;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                @Override
                public void run() {
                    progressDialog.show();
                    progressDialog.setProgress(0);
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.e("progress"," "+progress[0]);
            // Making progress bar visible
            progressDialog.show();
            // updating progress bar value
            progressDialog.setProgress(progress[0]);
            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try
            {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) total_upload_Size) * 100));
                            }
                        });

                FileBody fBody = new FileBody(file);
                //Log.e("fBody"," "+fBody);
                entity.addPart("file", fBody);
                entity.addPart("upload_preset",  new StringBody("daiglruo"));

                total_upload_Size = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200)
                {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                }
                else
                {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            }
            catch (ClientProtocolException e)
            {
                responseString = e.toString();
            }
            catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("TrimmerActivity", "Response from server: " + result);
            //super.onPostExecute(result);

            if(result != null)
            {
                if(result.contains("Error occurred! Http Status Code"))
                {
                    CommonFunctions.displayToast(context,result);
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                    }
                    finish();
                    return;
                }

                JSONObject object = null;

                try {
                    object = new JSONObject(result);
                    String public_id = object.getString("public_id");;
                    String createdAt = object.getString("created_at");;
                    String url = object.getString("url");
                    Log.d("public_id",public_id);
                    Log.d("url",url);

                    if(m_config.from_activity_record_video.equals("onboarding"))
                    {
                        ProfileIntro_Video_Request profileVideo_request = json_profilevideo_req(public_id, createdAt, url);
                        Intro_VideoAPI.req_intro_video_API(context, profileVideo_request);
                        if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                            progressDialog.cancel();
                        }
                    }
                    else if(m_config.from_activity_record_video.equals("thought"))
                    {
                        PostVideo_Request postVideo_request = json_post_video_upload(public_id, url, caption_value);
                        PostVideoAPI.req_post_video_api(context, postVideo_request);
                        if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                            progressDialog.cancel();
                        }
                    }


                } catch (JSONException e)
                {
                    e.printStackTrace();
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                    }
                    finish();
                }

            }
            else
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
                finish();
            }
        }

    }

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
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog();
    }
}