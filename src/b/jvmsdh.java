package b;final class jvmsdh extends Thread{public void run(){
	thdwatch._stop=true;
	if(!b.save_sessions_at_shutdown)return;
	try{session.all_save();}catch(final Throwable e){throw new Error(e);}
}}