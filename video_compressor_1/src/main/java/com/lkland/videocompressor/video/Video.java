package com.lkland.videocompressor.video;

public class Video implements IVideo{
	String _input_file_path;
	String mInName;
	String _output_file_path;
	String _output_file_name;
	String _output_file_size;
	int mDuration;
	public Video(){
	}
	@Override
	public void setInPath(String input_file_path){
        this._input_file_path = input_file_path;
	}
	@Override
    public void setOutPath(String output_file_path){
        this._output_file_path = output_file_path;
    }
    @Override
    public void setOutName(String output_file_name){
        this._output_file_name = output_file_name;
    }
    @Override
	public void setOutSize(String output_file_size){
		this._output_file_size = output_file_size;
	}

	@Override
	public String getInPath() {
		return _input_file_path;
	}

	@Override
	public String getOutPath() {
		return _output_file_path;
	}

	@Override
	public String getOutName() {
		return _output_file_name;
	}

	@Override
	public String getOutSize() {
		return _output_file_size;
	}
	@Override
	public void setDuration(int duration) {
		this.mDuration = duration;
	}
	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return this.mDuration;
	}
	@Override
	public void setInName(String inName) {
		mInName = inName;
	}
	@Override
	public String getInName() {
		return mInName;
	}
	@Override
	public String toString(){
		return getInName();
	}
}
