//package com.viintro.Viintro.VideoRecord;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.hardware.Camera;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.media.CamcorderProfile;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Environment;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.OrientationEventListener;
//import android.view.Surface;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.viintro.Viintro.Constants.Configuration_Parameter;
//import com.viintro.Viintro.Constants.Constcore;
//import com.viintro.Viintro.R;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;
//import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
//import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
//
///***
// *  TODO: 1. sound on/off
// *  2. resolution change
// * @author roman10
// *
// */
//
//public class VideoRecordingActivity_Work extends AppCompatActivity implements SensorEventListener, View.OnTouchListener, MediaRecorder.OnInfoListener
//{
//    MediaRecorder mMediaRecorder;
//    private Camera mCamera;
//    private CameraPreview mPreview;
//    private static boolean isRecording = false;
//
//
//    private FrameLayout framelayout;
//    private Button btn_facing, btn_flash, btn_capture,btn_Post, btn_Cancel;
//    private TextView txt_video_header, txt_upload_thought, txt_comment_characters, txt_timeremaining;
//    private RelativeLayout rel_profile_video, rel_sample_videos, rel_gallery_btn, rel_Comment, rel_select_mode, rel_overlay_thought;
//    private LinearLayout rel_add_link, rel_thought_gallery;
//    private ProgressBar circular_progress_bar;
//    private EditText edt_Comment;
//    private TextView mRecordDuration;
//    private static int totalmsec = 0;
//    private long timermaxval;
//    private long record_duration;
//    private CountDownTimer timer;
//    Context context;
//    public static File file_cache = null;
//    private static int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//    SensorManager sensorManager;
//    int[] orientation_mode = {1};
//    int rotationangle  = 0;
//    OrientationEventListener mOrientationListener;
//    private static float mDist = 0;
//    Boolean flashmode = false;
//    Configuration_Parameter m_config;
//    private FrameLayout preview;
//    public static Activity videorecording_activity;
//    private Boolean flag_onResumeCall = true;
//
//    public static void start(Context context)
//    {
//        Intent intent = new Intent(context, VideoRecordingActivity_Work.class);
//        context.startActivity(intent);
//        isRecording = false;
//        totalmsec = 0;
//        file_cache = null;
//        mDist = 0;
//        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
//
//        setContentView(R.layout.activity_record_video);
//
//        context = this;
//        videorecording_activity = this;
//        m_config = Configuration_Parameter.getInstance();
//        if(getIntent().getExtras() != null)
//        {
//            m_config.from_activity_record_video = getIntent().getExtras().getString("from");
//        }
//
//
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        initialiseUI();
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, Constcore.CAMERA_WRITE_EXTERNAL_RECORD);
//
//        } else
//        {
//
//        }
//
//
//        //Log.e("mCamera", " "+mCamera+" ");
//        // Create an instance of Camera
//        if(mCamera == null)
//        {
//            mCamera = getCameraInstance();
//            // Create our Preview view and set it as the content of our activity.
//            mPreview = new CameraPreview(this, mCamera);
//
//            preview = (FrameLayout) findViewById(R.id.camera_preview);
//            preview.addView(mPreview);
//
//            preview.setOnTouchListener(this);
//        }
//
//
//        btn_capture.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (isRecording) {
//                            // stop recording and release camera
//                            if(totalmsec >=  2000)
//                            {
//                                stoprecording();
//                            }
//
//                        }
//                        else
//                        {
//
//                            // initialize video camera
//                            if (prepareVideoRecorder()) {
//                                // Camera is available and unlocked, MediaRecorder is prepared,
//                                // now you can start recording
//                                try
//                                {
//                                    mMediaRecorder.start();
//                                    startTimer();
//                                    isRecording = true;
//                                    btn_facing.setVisibility(View.GONE);
//                                    btn_flash.setVisibility(View.GONE);
//                                    rel_sample_videos.setVisibility(View.GONE);
//                                    rel_gallery_btn.setVisibility(View.GONE);
//                                    rel_select_mode.setVisibility(View.GONE);
//                                }
//                                catch (Exception e)
//                                {
//                                    e.printStackTrace();
//                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//
//                            } else {
//                                // prepare didn't work, release the camera
//                                releaseMediaRecorder();
//
//                            }
//                        }
//                    }
//                });
//
//        btn_facing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                {
//                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//                    btn_flash.setVisibility(View.GONE);
//                }
//                else
//                {
//                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//                    btn_flash.setVisibility(View.VISIBLE);
//                }
//
//                if (mCamera != null) {
//                    mCamera.stopPreview();
//                    mCamera.release();
//                    preview.removeView(mPreview);
//                    mCamera = Camera.open(currentCameraId);
//                    setCameraDisplayOrientation(VideoRecordingActivity_Work.this, currentCameraId, mCamera);
//                    mPreview = new CameraPreview(context, mCamera);
//                    preview.addView(mPreview);
//
//                }
//
//            }
//        });
//
//
//        btn_flash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mCamera != null) {
//                    if (flashmode) {
//                        try {
//                            Camera.Parameters param = mCamera.getParameters();
//
//                            // Log.e("if flash on then off ", " " + flashmode);
//                            param.setFlashMode(flashmode ? Camera.Parameters.FLASH_MODE_TORCH
//                                    : Camera.Parameters.FLASH_MODE_OFF);
//                            mCamera.setParameters(param);
//                            flashmode = false;
//                        } catch (Exception e) {
//                            // TODO: handle exception
//                        }
//                    }
//                    else
//                    {
//                        try {
//                            Camera.Parameters param = mCamera.getParameters();
//                            //Log.e("if flash off then on ", " " + flashmode);
//                            //Log.e("!ConstsCore.flashmode", " " + flashmode);
//                            param.setFlashMode(flashmode ? Camera.Parameters.FLASH_MODE_TORCH
//                                    : Camera.Parameters.FLASH_MODE_OFF);
//                            mCamera.setParameters(param);
//                            flashmode = true;
//                        } catch (Exception e) {
//                            // TODO: handle exception
//                        }
//                    }
//
//                }
//
//            }
//        });
//
//    }
//
//    private void initialiseUI() {
//
//        framelayout = (FrameLayout) findViewById(R.id.camera_preview);
//        btn_facing = (Button) findViewById(R.id.btn_facing);
//        btn_flash = (Button) findViewById(R.id.btn_flash);
//        txt_video_header = (TextView) findViewById(R.id.txt_video_header);
//        txt_upload_thought = (TextView) findViewById(R.id.txt_upload_thought);
//        txt_comment_characters = (TextView) findViewById(R.id.txt_comment_characters);
//        rel_profile_video = (RelativeLayout) findViewById(R.id.rel_profile_video);
//        rel_sample_videos = (RelativeLayout) findViewById(R.id.rel_sample_videos);
//        btn_capture = (Button) findViewById(R.id.btn_capture);
//        circular_progress_bar = (ProgressBar) findViewById(R.id.circular_progress_bar);
//        rel_gallery_btn = (RelativeLayout) findViewById(R.id.rel_gallery_btn);
//        mRecordDuration = (TextView) findViewById(R.id.recordDuration);
//        txt_timeremaining = (TextView) findViewById(R.id.txt_timeremaining);
//        rel_Comment = (RelativeLayout) findViewById(R.id.rel_Comment);
//        edt_Comment = (EditText) findViewById(R.id.edt_Comment);
//        btn_Post = (Button) findViewById(R.id.btn_Post);
//        rel_overlay_thought = (RelativeLayout) findViewById(R.id.rel_overlay_thought);
//        rel_select_mode = (RelativeLayout) findViewById(R.id.rel_select_mode);
//        rel_add_link = (LinearLayout) findViewById(R.id.rel_add_link);
//        rel_thought_gallery = (LinearLayout) findViewById(R.id.rel_thought_gallery);
//        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
//
//        //String m_config.fromwhere = getIntent().getExtras().getString("from");
//
//        if (m_config.from_activity_record_video.equals("onboarding"))
//        {
//            txt_upload_thought.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rel_profile_video.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            rel_profile_video.setLayoutParams(params);
//            rel_Comment.setVisibility(View.GONE);
//            rel_overlay_thought.setVisibility(View.GONE);
//            rel_select_mode.setVisibility(View.GONE);
//            txt_comment_characters.setVisibility(View.GONE);
//            timermaxval = Constcore.record_time_onboarding;
//            record_duration = Constcore.record_time_onboarding;
//            txt_timeremaining.setText((record_duration/1000)+"\nsec");
//
//        }
//        else if (m_config.from_activity_record_video.equals("thought"))
//        {
//            txt_video_header.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rel_profile_video.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            rel_profile_video.setLayoutParams(params);
//            rel_Comment.setVisibility(View.GONE);
//            rel_sample_videos.setVisibility(View.GONE);
//            rel_gallery_btn.setVisibility(View.GONE);
//            rel_overlay_thought.setVisibility(View.GONE);
//            txt_comment_characters.setVisibility(View.GONE);
//            timermaxval = Constcore.record_time_thought;
//            record_duration = Constcore.record_time_thought;
//            txt_timeremaining.setText((record_duration/1000)+"\nsec");
//
//        }
//        else if (m_config.from_activity_record_video.equals("comment"))
//        {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) framelayout.getLayoutParams();
//            params.addRule(RelativeLayout.ABOVE, R.id.rel_profile_video);
//            framelayout.setLayoutParams(params);
//            txt_video_header.setVisibility(View.GONE);
//            rel_sample_videos.setVisibility(View.GONE);
//            txt_upload_thought.setVisibility(View.GONE);
//            rel_overlay_thought.setVisibility(View.GONE);
//            rel_select_mode.setVisibility(View.GONE);
//            timermaxval = Constcore.record_time_comment;
//            record_duration = Constcore.record_time_comment;
//            txt_timeremaining.setText((record_duration/1000)+"\nsec");
//
//        }
//
//        rel_select_mode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableframeclick();
//                rel_select_mode.setVisibility(View.GONE);
//                rel_overlay_thought.setVisibility(View.VISIBLE);
//            }
//        });
//
//        btn_Cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                enableframeclick();
//                rel_select_mode.setVisibility(View.GONE);
//                rel_overlay_thought.setVisibility(View.GONE);
//            }
//        });
//
//
//        rel_sample_videos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent int_Samplevideo = new Intent(VideoRecordingActivity_Work.this, SampleVideosActivity.class);
//                startActivity(int_Samplevideo);
//            }
//        });
//
//        rel_gallery_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rel_overlay_thought.setVisibility(View.GONE);
//                enableframeclick();
//                pickFromGallery(context);
//
//            }
//        });
//
//        rel_thought_gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rel_select_mode.setVisibility(View.VISIBLE);
//                enableframeclick();
//                pickFromGallery(context);
//            }
//        });
//
//        rel_add_link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rel_overlay_thought.setVisibility(View.GONE);
//                rel_select_mode.setVisibility(View.VISIBLE);
//                enableframeclick();
//                Intent int_addlink = new Intent(VideoRecordingActivity_Work.this, AddLinkActivity.class);
//                startActivity(int_addlink);
//            }
//        });
//
//        //Log.e("initialise orientation", " " + orientation_mode[0]);
//    }
//
//
//    private boolean prepareVideoRecorder(){
//
//
//        mMediaRecorder = new MediaRecorder();
//        mMediaRecorder.setOnInfoListener(this);
//
//        // Step 1: Unlock and set camera to MediaRecorder
//        if(mCamera != null)
//        {
//            try
//            {
//                mCamera.unlock();
//                mMediaRecorder.setCamera(mCamera);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        // Step 2: Set sources
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//
//        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
//        //mMediaRecorder.setProfile(mProfile);
//
//        //Step 3: config
//        mMediaRecorder.setOutputFormat(mProfile.fileFormat);
//        mMediaRecorder.setAudioEncoder(mProfile.audioCodec);
//        mMediaRecorder.setVideoEncoder(mProfile.videoCodec);
//        file_cache  = getStorageFile(MEDIA_TYPE_VIDEO);
//        //Log.e("file_cache "," "+file_cache.toString() +" \n=== ");
//
//        //Set output file
//        mMediaRecorder.setOutputFile(file_cache.toString());
//        mMediaRecorder.setVideoSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
//        mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);
//        mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
//        mMediaRecorder.setAudioEncodingBitRate(mProfile.audioBitRate);
//        mMediaRecorder.setAudioSamplingRate(mProfile.audioSampleRate);
//        mMediaRecorder.setAudioChannels(mProfile.audioChannels);
//        if(m_config.from_activity_record_video.equals("onboarding"))
//        {
//            mMediaRecorder.setMaxDuration((int) Constcore.record_time_onboarding);
//        }
//        else if(m_config.from_activity_record_video.equals("thought"))
//        {
//            mMediaRecorder.setMaxDuration((int) Constcore.record_time_thought);
//        }
//        else if(m_config.from_activity_record_video.equals("comment"))
//        {
//            mMediaRecorder.setMaxDuration((int) Constcore.record_time_comment);
//        }
//
//
//
//        //Log.e("=== "," "+mProfile.fileFormat+" "+mProfile.audioCodec+" "+mProfile.videoCodec+" "+mProfile.videoFrameWidth+" "+ mProfile.videoFrameHeight+" "+mProfile.videoFrameRate+" "+mProfile.videoBitRate+ " "+mProfile.audioBitRate);
//        // Step 5: Set the preview output
//        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
//
//        //Log.e("currentCameraId"," "+currentCameraId);
//
//        //check whether auto rotate is on/off
//        if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
//        {
//            //Toast.makeText(getApplicationContext(), "Auto Rotate is ON", Toast.LENGTH_SHORT).show();
//            //**// orientation for video storage
//
//            Log.e("orientation_mode[0]"," "+orientation_mode[0]);
//            Log.e("rotationangle"," "+rotationangle);
//
//            // check orientation mode (portrait mode)
//            if(orientation_mode[0] == 1)
//            {
//                // check orientation listener angle (0/360 = normal portrait)
//                if(rotationangle <= 45 && rotationangle >= 0 || rotationangle <= 360 && rotationangle > 315)
//                {
//                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                    {
//                        mMediaRecorder.setOrientationHint(90);
//                    }
//                    else
//                    {
//                        mMediaRecorder.setOrientationHint(270);
//                    }
//
//                }
//                // check orientation listener angle (180 = downside portrait)
//                else if(rotationangle <= 225 && rotationangle > 135)
//                {
//                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                    {
//                        mMediaRecorder.setOrientationHint(270);
//                    }
//                    else
//                    {
//                        mMediaRecorder.setOrientationHint(90);
//                    }
//                }
//
//
//            }
//            // check orientation mode (landscape mode)
//            if(orientation_mode[0] == 2)
//            {
//                // check orientation listener angle (90 = left landscape)
//                if (rotationangle <= 135 && rotationangle > 45)
//                {
//                    mMediaRecorder.setOrientationHint(180);
//                }
//                // check orientation listener angle (270 = right landscape)
//                else if(rotationangle <= 315 && rotationangle > 225)
//                {
//                    mMediaRecorder.setOrientationHint(0);
//                }
//            }
//
//
//        }
//        else
//        {
//
//            //Toast.makeText(getApplicationContext(), "Auto Rotate is OFF", Toast.LENGTH_SHORT).show();
//            //Log.e("orientation_mode[0]"," "+orientation_mode[0]);
//
//            if(orientation_mode[0] == 1)
//            {
//                // check orientation listener angle (0/360 = normal portrait)
//                if(rotationangle <= 45 && rotationangle >= 0 || rotationangle <= 360 && rotationangle > 315)
//                {
//                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                    {
//                        mMediaRecorder.setOrientationHint(90);
//                    }
//                    else
//                    {
//                        mMediaRecorder.setOrientationHint(270);
//                    }
//
//                }
//                // check orientation listener angle (180 = downside portrait)
//                else if(rotationangle <= 225 && rotationangle > 135)
//                {
//                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                    {
//                        mMediaRecorder.setOrientationHint(270);
//                    }
//                    else
//                    {
//                        mMediaRecorder.setOrientationHint(90);
//                    }
//                }
//
//
//            }
//            // check orientation mode (landscape mode)
//            if(orientation_mode[0] == 2)
//            {
//                // check orientation listener angle (90 = left landscape)
//                if (rotationangle <= 135 && rotationangle > 45)
//                {
//                    mMediaRecorder.setOrientationHint(180);
//                }
//                // check orientation listener angle (270 = right landscape)
//                else if(rotationangle <= 315 && rotationangle > 225)
//                {
//                    mMediaRecorder.setOrientationHint(0);
//                }
//            }
//
//        }
//
//        // Step 6: Prepare configured MediaRecorder
//        try
//        {
//            Log.e("prepare"," ");
//            mMediaRecorder.prepare();
//        }
//        catch (IllegalStateException e)
//        {
//            Log.d("", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        }
//        catch (IOException e)
//        {
//            Log.d("", "IOException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        }
//        return true;
//    }
//
//    /** Create a File for saving video in Internal Storage **/
//    private File getStorageFile(int type){
//
//        File mediaStorageDir;
//
//        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if(isSDPresent)
//        {
////            mediaStorageDir = new File(Environment.getExternalStorageDirectory()
////                    + "/Android/data/videodemo.aperotechnologies.com.customcamera/cache");
//
//            mediaStorageDir =  new File(getExternalFilesDir(null), "videos");
//        }
//        else
//        {
//            mediaStorageDir =  new File(getExternalFilesDir(null), "videos");
//        }
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else if(type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_"+ timeStamp + ".mp4");
//
//            //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + MediaRecorder.OutputFormat.DEFAULT);
//
//        } else
//        {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
//
//    /** A safe way to get an instance of the Camera object. */
//    public Camera getCameraInstance(){
//        Camera c = null;
//        try {
//
//            c = Camera.open(); // attempt to get a Camera instance
//            setCameraDisplayOrientation((Activity) context, currentCameraId, c);
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }
//
//    private void releaseMediaRecorder(){
//        if (mMediaRecorder != null) {
//            mMediaRecorder.reset();   // clear recorder configuration
//            mMediaRecorder.release(); // release the recorder object
//            mMediaRecorder = null;
//            mCamera.lock();           // lock camera for later use
//        }
//    }
//
//    private void releaseCamera(){
//        if (mCamera != null){
//            mCamera.release();        // release the camera for other applications
//            mCamera = null;
//        }
//    }
//
//    public void setCameraDisplayOrientation(Activity activity,
//                                            int cameraId, Camera camera) {
//        Camera.CameraInfo info =
//                new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay()
//                .getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0: degrees = 0; break;
//            case Surface.ROTATION_90: degrees = 90; break;
//            case Surface.ROTATION_180: degrees = 180; break;
//            case Surface.ROTATION_270: degrees = 270; break;
//        }
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//
//        camera.setDisplayOrientation(result);
//
//    }
//
//    public  int getPreviewOrientation(Context context, int cameraId) {
//
//        Camera.CameraInfo info =
//                new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//
//        int temp;
//        int previewOrientation;
//
//        int deviceOrientation = getDeviceOrientation(context);
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            temp = (info.orientation + deviceOrientation) % 360;
//            previewOrientation = (360 - temp) % 360;  // compensate the mirror
//        } else {  // back-facing
//            previewOrientation = (info.orientation - deviceOrientation + 360) % 360;
//        }
//
//        //Log.e("prevOrientation "," "+previewOrientation);
//        return previewOrientation;
//    }
//
//    public int getDeviceOrientation(Context context) {
//
//        int degrees = 0;
//        WindowManager windowManager =
//                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        int rotation = windowManager.getDefaultDisplay().getRotation();
//
//        switch(rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        //Log.e("device orientation", " "+degrees);
//        return degrees;
//    }
//
//    private void requestPermission(final String permission, String rationale, final int requestCode) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Permission needed");
//            builder.setMessage(rationale);
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
//                }
//            });
//            builder.setNegativeButton("Cancel", null);
//            builder.show();
//        } else {
//            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
//        }
//    }
//
//    public void pickFromGallery(Context context) {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Permission needed", Constcore.REQUEST_STORAGE_READ_ACCESS_PERMISSION);
//        } else {
//
//            Intent intent = new Intent();
//            intent.setTypeAndNormalize("video/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            startActivityForResult(Intent.createChooser(intent, "Select Video"), Constcore.OPEN_GALLERY);
//
//        }
//    }
//
//    public void startTimer() {
//
//        timer = new CountDownTimer(timermaxval, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//
//
//                //Log.e("millisUntilFinished"," "+millisUntilFinished);
//                long time = 0;
//                int time_per_percent = 0;
//                if(m_config.from_activity_record_video.equals("onboarding"))
//                {
//                    time = Constcore.record_time_onboarding;
//                    time_per_percent = (int) (time/100);
//                }
//                else if(m_config.from_activity_record_video.equals("thought"))
//                {
//                    time = Constcore.record_time_thought;
//                    time_per_percent = (int) (time/100);
//                }
//                else if(m_config.from_activity_record_video.equals("comment"))
//                {
//                    time = Constcore.record_time_comment;
//                    time_per_percent = (int) (time/100);
//                }
//
//
//                Long timeinmsec = time - millisUntilFinished;
//                timermaxval = millisUntilFinished;
//
//                mRecordDuration.setText(TimeUnit.MILLISECONDS.toMinutes(timeinmsec) + ":" +
//                        (TimeUnit.MILLISECONDS.toSeconds(timeinmsec) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeinmsec)) + 1));
//                // harshada
//                int mintomsec = Integer.parseInt(mRecordDuration.getText().toString().split(":")[0]) * 60 * 1000;
//                int sectomsec = Integer.parseInt(mRecordDuration.getText().toString().split(":")[1]) * 1000;
//                totalmsec = mintomsec + sectomsec;
//                Log.e("totalmsec ", " " + totalmsec);
//
//                if (totalmsec / time_per_percent > 0) {
//                    circular_progress_bar.setProgress(totalmsec/ time_per_percent);
//                }
//
//                String timereminmin = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) + ":" +
//                        (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
//                //txtTimer.setText(timereminmin);
//                txt_timeremaining.setText(String.valueOf((int) millisUntilFinished/1000) + "\nsec");
//
//            }
//
//            public void onFinish() {
//
////                //txtTimer.setText("0:00");
////                txt_timeremaining.setText("0\nsec");
////                //mRecordDuration.setText("02:00");
////                circular_progress_bar.setProgress(100);
////                timermaxval = 0;
////                totalmsec = (int) record_duration;
////                stoprecording();
//            }
//        };
//
//        timer.start();
//
//    }
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        switch (requestCode) {
//
//            case Constcore.OPEN_GALLERY:
//
//                if (intent == null) {
//                    return;
//                }
//                final Uri selectedUri = intent.getData();
//
//                Log.e("selectedUri", " " + selectedUri);
//                if (selectedUri != null) {
//
//                    startTrimActivity(selectedUri, "fromGallery");
//
//                    String filePath = FileUtils.getPath(this, selectedUri);
//                    Log.e("filePath ", " " + filePath);
//
//
//
//
//                } else {
//                    Toast.makeText(VideoRecordingActivity_Work.this, "Cannot retrieve video", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    private void startTrimActivity(@NonNull Uri uri, String from) {
//        releaseCamera();
//        finish();
//        Intent intent = new Intent(this, TrimmerActivity.class);
//        intent.putExtra(Constcore.EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
//        intent.putExtra("maxDuration",record_duration/1000);
//        intent.putExtra("from",from);
//        startActivity(intent);
//
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case Constcore.CAMERA_WRITE_EXTERNAL_RECORD:
//                Log.e("grantResults "," "+grantResults.length);
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
//                {
//                    VideoRecordingActivity_Work.start(context);
//                    finish();
//                }
//                else
//                {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, Constcore.CAMERA_WRITE_EXTERNAL_RECORD);
//                }
//                break;
//
//            case Constcore.REQUEST_STORAGE_READ_ACCESS_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    pickFromGallery(context);
//                }
//                break;
//
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    public void stoprecording()
//    {
//        try
//        {
//            mMediaRecorder.stop();  // stop the recording
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        releaseMediaRecorder(); // release the MediaRecorder object
//        mCamera.lock();         // take camera access back from MediaRecorder
//        if (timer != null) {
//            timer.cancel();
//            //circular_progress_bar.setProgress(totalmsec/1200);
//        }
//        btn_facing.setVisibility(View.VISIBLE);
//        btn_flash.setVisibility(View.VISIBLE);
//        if(m_config.from_activity_record_video.equals("onboarding"))
//        {
//            rel_sample_videos.setVisibility(View.VISIBLE);
//        }
//        if(m_config.from_activity_record_video.equals("onboarding"))
//        {
//            rel_gallery_btn.setVisibility(View.VISIBLE);
//        }
//
//        if(m_config.from_activity_record_video.equals("thought"))
//        {
//            rel_select_mode.setVisibility(View.VISIBLE);
//        }
//
//        // inform the user that recording has stopped
//        //captureButton.setText("Capture");
//        isRecording = false;
//        totalmsec = 0;
//        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//        Uri uri = Uri.fromFile(file_cache);
//        flag_onResumeCall = false;
//        startTrimActivity(uri, "fromCamera");
//    }
//
//    @Override
//    public void onBackPressed() {
//        Log.e(" ","onbackpressed");
//        if(rel_overlay_thought.getVisibility() == View.VISIBLE)
//        {
//            enableframeclick();
//            rel_overlay_thought.setVisibility(View.GONE);
//            rel_select_mode.setVisibility(View.VISIBLE);
//            return;
//        }
//
//        file_cache = null;
//        isRecording = false;
//        totalmsec = 0;
//        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//        releaseMediaRecorder();
//        releaseCamera();
//        finish();
//
//    }
//
//    @Override
//    protected void onResume() {
//
//        Log.e("onResume","call");
//        super.onResume();
//        restartVideoonResume(context);
//        // register this class as a listener for the orientation and
//        // accelerometer sensors
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);
//
//        // Gives orientation angle of device
//        mOrientationListener = new OrientationEventListener(this,
//                SensorManager.SENSOR_DELAY_NORMAL) {
//
//            @Override
//            public void onOrientationChanged(int orientation) {
//                ;;Log.v("", "Orientation changed to " + orientation);
//                rotationangle = orientation;
//
//
//            }
//        };
//        mOrientationListener.enable();
//
//
//
//    }
//
//    @Override
//    protected void onPause() {
//        //Log.e("onPause","call");
//        // unregister listener
//        super.onPause();
//        sensorManager.unregisterListener(this);
//        mOrientationListener.disable();
//    }
//
//    @Override
//    protected void onStop() {
//        //Log.e("onStop","call");
//        super.onStop();
//        restartVideoonStop(context);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent)
//    {
//        int orientation = -1;
//        if (sensorEvent.values[1]<6.5 && sensorEvent.values[1]>-6.5)
//        {
//            if (orientation!=1) {
//                //Toast.makeText(getApplicationContext(), "Landscape", Toast.LENGTH_SHORT).show();
//                orientation_mode[0] = 2;
//                setImage_Rotation();
//            }
//            orientation = 1;
//        }
//        else
//        {
//            if (orientation!=0) {
//                //Toast.makeText(getApplicationContext(), "Portrait", Toast.LENGTH_SHORT).show();
//                orientation_mode[0] = 1;
//                setImage_Rotation();
//            }
//            orientation = 0;
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i)
//    {
//    }
//
//    /** when autorotate is "ON" onCreate method is called everytime whenever orientation changes
//     so to restrict onCreate method we have used (android:configChanges="orientation|keyboardHidden|screenSize") to particular activity
//     **/
//    @Override
//    public void onConfigurationChanged(Configuration newConfig)
//    {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
//        {
//            //your code
//            // here we have set orientation angle to camera
//            setCameraDisplayOrientation(VideoRecordingActivity_Work.this, currentCameraId, mCamera);
//
//        }
//        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//            //your code
//            // here we have set orientation angle to camera
//            setCameraDisplayOrientation(VideoRecordingActivity_Work.this, currentCameraId, mCamera);
//        }
//    }
//
//    private void handleZoom(MotionEvent event, Camera.Parameters params) {
//        int maxZoom = params.getMaxZoom();
//        int zoom = params.getZoom();
//        float newDist = getFingerSpacing(event);
//        if (newDist > mDist) {
//            //zoom in
//            if (zoom < maxZoom)
//                zoom++;
//        } else if (newDist < mDist) {
//            //zoom out
//            if (zoom > 0)
//                zoom--;
//        }
//        mDist = newDist;
//        params.setZoom(zoom);
//        mCamera.setParameters(params);
//    }
//
//    public void handleFocus(MotionEvent event, Camera.Parameters params) {
//        int pointerId = event.getPointerId(0);
//        int pointerIndex = event.findPointerIndex(pointerId);
//        // Get the pointer's current position
//        float x = event.getX(pointerIndex);
//        float y = event.getY(pointerIndex);
//
//        List<String> supportedFocusModes = params.getSupportedFocusModes();
//        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//            mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                @Override
//                public void onAutoFocus(boolean b, Camera camera) {
//                    // currently set to auto-focus on single touch
//                }
//            });
//        }
//    }
//
//    /** Determine the space between the first two fingers */
//    private float getFingerSpacing(MotionEvent event) {
//        // ...
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return (float) Math.sqrt(x * x + y * y);
//    }
//
//    @Override
//    public boolean onTouch(View view, MotionEvent event) {
//        // Get the pointer ID
//        if(mCamera != null) {
//            Camera.Parameters params = mCamera.getParameters();
//            int action = event.getAction();
//
//            //Log.e("getPointerCount "," "+event.getPointerCount());
//            //Log.e("action "," "+(action == MotionEvent.ACTION_POINTER_DOWN) +" "+(action == MotionEvent.ACTION_MOVE)+" "+(action == MotionEvent.ACTION_UP));
//
//
//            if (event.getPointerCount() > 1) {
//                // handle multi-touch events
//                if (action == MotionEvent.ACTION_POINTER_DOWN) {
//                    mDist = getFingerSpacing(event);
//                } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
//                    mCamera.cancelAutoFocus();
//                    handleZoom(event, params);
//                }
//            } else {
//                // handle single touch events
//                if (action == MotionEvent.ACTION_UP) {
//                    handleFocus(event, params);
//                }
//            }
//        }
//        return true;
//    }
//
//
//    private void disableframeclick()
//    {
////        btn_facing.setClickable(false);
////        btn_flash.setClickable(false);
////        btn_capture.setClickable(false);
////        rel_select_mode.setClickable(false);
//        btn_capture.setClickable(false);
//        btn_facing.setVisibility(View.GONE);
//        btn_flash.setVisibility(View.GONE);
//
//    }
//
//    private void enableframeclick()
//    {
////        btn_facing.setClickable(true);
////        btn_flash.setClickable(true);
////        btn_capture.setClickable(true);
////        rel_select_mode.setClickable(true);
//        btn_capture.setClickable(true);
//        btn_facing.setVisibility(View.VISIBLE);
//        btn_flash.setVisibility(View.VISIBLE);
//        btn_capture.setVisibility(View.VISIBLE);
//
//
//    }
//
//    private void setImage_Rotation()
//    {
//        if(rotationangle <= 45 && rotationangle >= 0 || rotationangle <= 360 && rotationangle > 315)
//        {
//            btn_facing.setRotation(180);
//        }
//        else if(rotationangle <= 225 && rotationangle > 135)
//        {
//            btn_facing.setRotation(0);
//        }
//        else if (rotationangle <= 135 && rotationangle > 45)
//        {
//            btn_facing.setRotation(90);
//        }
//        else if(rotationangle <= 315 && rotationangle > 225)
//        {
//            btn_facing.setRotation(270);
//        }
//
//
////        if(rotationangle <= 45 && rotationangle >= 0 || rotationangle <= 360 && rotationangle > 315)
////        {
////            btn_flash.setRotation(180);
////
////
////        }
////        else if(rotationangle <= 225 && rotationangle > 135)
////        {
////            btn_flash.setRotation(0);
////
////
////        }
////        else if (rotationangle <= 135 && rotationangle > 45)
////        {
////            btn_flash.setRotation(90);
////
////
////        }
////        else if(rotationangle <= 315 && rotationangle > 225)
////        {
////
////            btn_flash.setRotation(270);
////
////
////        }
//
//
//    }
//
//
//    @Override
//    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
//
//        if(i == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
//        {
//            txt_timeremaining.setText("0\nsec");
//            circular_progress_bar.setProgress(100);
//            timermaxval = 0;
//            totalmsec = 0;
//            stoprecording();
//        }
//    }
//
//    public void restartVideoonResume(Context context) {
//        if (isRecording && totalmsec >=  2000 && flag_onResumeCall == true)
//        {
////            try {
////                mMediaRecorder.stop();  // stop the recording
////            } catch (Exception e) {
////                e.printStackTrace();
////                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
////                return;
////            }
////
////            releaseMediaRecorder(); // release the MediaRecorder object
////            mCamera.lock();         // take camera access back from MediaRecorder
////            if (timer != null) {
////                timer.cancel();
////            }
//
//            VideoRecordingActivity_Work.start(context);
//        }
//    }
//
//
//    public void restartVideoonStop(Context context) {
//        if (isRecording && totalmsec >=  2000 && flag_onResumeCall == true)
//        {
//            try {
//                mMediaRecorder.stop();  // stop the recording
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            releaseMediaRecorder(); // release the MediaRecorder object
//            mCamera.lock();         // take camera access back from MediaRecorder
//            if (timer != null) {
//                timer.cancel();
//            }
//
//        }
//    }
//}