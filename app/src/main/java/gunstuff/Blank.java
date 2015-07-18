//package gunstuff;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.media.MediaPlayer;
//
////import android.view.Menu;
////
////import android.view.View.OnClickListener;
////
////import java.util.Queue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//
///**
// * Created by george on 7/2/15.
// */
//public class Blank extends Activity {
////    public Button _partybutton;
////    Boolean partyOn = false;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // The activity is being created.
//
//        //links java code to xml code
//        setContentView(R.layout.blankactiv);
//
//        //all this code does is change the text of a button from off to on
////        _partybutton = (Button) findViewById(R.id.partybutton);
////        _partybutton.setOnClickListener(new View.OnClickListener() {
////            public void onClick(View v) {
////                partyOn = !partyOn;
////                Button mButton = (Button) findViewById(R.id.partybutton);
////                if (partyOn == false) {
////                    mButton.setText("OFF");
////                } else {
////                    mButton.setText("ON");
////                }
////
////            }
////        });
////        letsParty();
//
//        Button boton = (Button) findViewById(R.id.boton);
//
//        final int singleShot = R.raw.single_shot;
//        final Context finalThis = this;
//
//        final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();
//
//        for (int i = 0; i < 50; i++) {
//            new Thread() {
//                public void run() {
//                    while (true) {
//                        try {
//                            Runnable task = shotQ.take();
//                            task.run();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
//        }
//
//        boton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Runnable task = new Runnable() {
//                    public void run() {
//                        MediaPlayer mp = MediaPlayer.create(finalThis, singleShot);
//                        mp.start();
//                        try {
//                            Thread.sleep(1429);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mp.release();
//                    }
//                };
//                shotQ.add(task);
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // The activity is about to become visible.
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // The activity has become visible (it is now "resumed").
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Another activity is taking focus (this activity is about to be "paused").
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // The activity is no longer visible (it is now "stopped")
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // The activity is about to be destroyed.
//    }
//
//    //letsParty doesn't work
////    public void letsParty() {
////        Button mButton = (Button) findViewById(R.id.partybutton);
////        mButton.setText("On");
////    }
//}