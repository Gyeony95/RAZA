package fr.pchab.webrtcclient;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.EGLContext;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.webrtc.*;

import static android.content.Context.MODE_PRIVATE;

public class WebRtcClient {
    private final static String TAG = "duongnx";
    private final static int MAX_PEER = 2;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private PeerConnectionFactory factory;
    private HashMap<String, Peer> peers = new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    private RtcListener mListener;
    private Socket client;
    SharedPreferences preferences;
    String myname;
    /**
     * Implement this interface to be notified of events.
     */
    public interface RtcListener {
        void onCallReady(String callId);

        void onExchangeReady(String callId);


        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onRemoveRemoteStream(int endPoint);

        void onChangeConnecttrue();
    }

    private interface Command {
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateOfferCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "CreateOfferCommand");
            Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }
    }

    private class CreateAnswerCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "CreateAnswerCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "SetRemoteSDPCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "AddIceCandidateCommand");
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("id"),
                        payload.getInt("label"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    /**
     * Send a message through the signaling server
     *
     * @param to      id of recipient
     * @param type    type of message
     * @param payload payload of message
     * @throws JSONException
     */
    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        client.emit("message", message);
        Log.d(TAG, "WebRtcClient sendMessage:" + message.toString());
    }

//    public void exchange_id(String my_id){
//        Log.e("WebRtcClient", "에밋!");
//        client.emit("exchange", my_id);
//        Log.e("WebRtcClient", "에밋!222");
//
//    }

    private class MessageHandler {
        private HashMap<String, Command> commandMap;

        private MessageHandler() {
            this.commandMap = new HashMap<>();
            commandMap.put("init", new CreateOfferCommand());
            commandMap.put("offer", new CreateAnswerCommand());
            commandMap.put("answer", new SetRemoteSDPCommand());
            commandMap.put("candidate", new AddIceCandidateCommand());
        }

        private Emitter.Listener onMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "update전");

                try {
                    JSONObject message = new JSONObject();
                    message.put("name", myname);
                    message.put("connect", false);
                    client.emit("update", message);
                    Log.e(TAG, "update :" + message.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "update실패");

                }
                Log.e(TAG, "update후");



                Log.e("test", "onMessage");
                JSONObject data = (JSONObject) args[0];
                Log.d("duongnx", "onMessage:call " + data);
                try {
                    String from = data.getString("from");
                    String type = data.getString("type");
                    JSONObject payload = null;
                    if (!type.equals("init")) {
                        payload = data.getJSONObject("payload");
                    }
                    // if peer is unknown, try to add him
                    if (!peers.containsKey(from)) {
                        // if MAX_PEER is reach, ignore the call
                        int endPoint = findEndPoint();
                        if (endPoint != MAX_PEER) {
                            Peer peer = addPeer(from, endPoint);
                            peer.pc.addStream(localMS);
                            commandMap.get(type).execute(from, payload);
                        }
                    } else {
                        commandMap.get(type).execute(from, payload);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };



        private Emitter.Listener onId = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("test", "onId");

                String id = (String) args[0];
                Log.d("duongnx", "onId:call " + id);
                mListener.onCallReady(id);
            }
        };


        private Emitter.Listener test = new Emitter.Listener() {
            @Override
            public void call(Object... args) {


            }
        };


    }

    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs

            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
                Log.d(TAG, "SdpObserver onCreateSuccess:" + payload.toString());
                sendMessage(id, sdp.type.canonicalForm(), payload);
                pc.setLocalDescription(Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mListener.onExchangeReady(id);
            Log.e("webrtccloent", id);
        }

        @Override
        public void onSetSuccess() {
            Log.d(TAG, "SdpObserver onSetSuccess:");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "SdpObserver onCreateFailure:" + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "SdpObserver onSetFailure:" + s);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.d(TAG, "PeerConnection.Observer onIceConnectionReceivingChange:" + b);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d(TAG, "PeerConnection.Observer onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d(TAG, "PeerConnection.Observer onIceConnectionChange:" + iceConnectionState.toString());
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");

            }
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d(TAG, "PeerConnection.Observer onIceConnectionChange:" + iceGatheringState.toString());
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("label", candidate.sdpMLineIndex);
                payload.put("id", candidate.sdpMid);
                payload.put("candidate", candidate.sdp);
                Log.d(TAG, "PeerConnection.Observer onIceCandidate:" + candidate.toString());
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "PeerConnection.Observer onAddStream:" + mediaStream.label());
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            mListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "PeerConnection.Observer onRemoveStream:" + mediaStream.label());
            removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.d(TAG, "PeerConnection.Observer onDataChannel:" + dataChannel.toString());
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d(TAG, "PeerConnection.Observer onRenegotiationNeeded:");
        }

        public Peer(String id, int endPoint) {
            Log.d(TAG, "new Peer: " + id + " " + endPoint);
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;

            pc.addStream(localMS); //, new MediaConstraints()

            mListener.onStatusChanged("CONNECTING");
        }
    }

    private Peer addPeer(String id, int endPoint) {
        Log.d(TAG, "addPeer :" + id);
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    private void removePeer(String id) {
        Log.d(TAG, "removePeer :" + id);
        Peer peer = peers.get(id);

        Log.e("webrtcclient", peers.toString() );

        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.pc.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }

    public WebRtcClient(RtcListener listener, String host, PeerConnectionParameters params, EglBase.Context mEGLcontext) {
        Log.d(TAG, "WebRtcClient host:" + host + " connect to socket");
        mListener = listener;
        pcParams = params;
        PeerConnectionFactory.initializeAndroidGlobals(listener, true, true,
                params.videoCodecHwAcceleration);
        factory = new PeerConnectionFactory();
        MessageHandler messageHandler = new MessageHandler();

        try {
            client = IO.socket(host);
        } catch (URISyntaxException e) {
            Log.d("duongnx", "URISyntaxException:" + e.getMessage());
            e.printStackTrace();
        }
        client.on("id", messageHandler.onId);
        client.on("message", messageHandler.onMessage);
        //client.on("exchange", messageHandler.test);
        //client.on("test", messageHandler.test);
        client.connect();

        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    }

    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();

    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy(String id) {
        Log.e("webclient", "1");
        for (Peer peer : peers.values()) {
            //peer.pc.dispose();
        }

        removePeer(id);
        Log.e("webclient", "2");

        if (videoSource != null)
            videoSource.stop();
        Log.e("webclient", "3");

        factory.dispose();

        Log.e("webclient", "4");

        client.disconnect();
        Log.e("webclient", "5");

        client.close();
        Log.e("webclient", "6");

    }

    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    /**
     * Start the client.
     * <p>
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.
     *
     * @param name client name
     */
    public void start(String name, Boolean connect) {
        setCamera();
        myname = name;
        try {
            JSONObject message = new JSONObject();
            message.put("name", name);
            message.put("connect", connect);
            client.emit("readyToStream", message);
            Log.e(TAG, "WebRtcClient start:" + message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCamera() {
        Log.e(TAG, "WebRtcClient setCamera:");
        if(localMS == null){
            Log.e("asd", "널");
        }else{
            Log.e("asd", "널ㄴㄴ");
        }
        if(factory == null){
            Log.e("asd", "널22");
        }else{
            Log.e("asd", "널ㄴㄴ22");
        }
        localMS = null;
        Log.e("asd", factory.toString());
        Log.e("asd", factory.getClass().getName());
        Log.e("asd", factory.getClass().getCanonicalName());
        Log.e("asd", factory.getClass().getSimpleName());
        Log.e("asd", factory.getClass().toString());

        localMS = factory.createLocalMediaStream("ARDAMS");
        Log.e(TAG, "WebRtcClient setCamera:222");

        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoSource = factory.createVideoSource(getVideoCapturer(), videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }
        Log.e(TAG, "WebRtcClient setCamera:333");

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        if (audioSource != null)
            localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));
        Log.e(TAG, "WebRtcClient setCamera:444");

        mListener.onLocalStream(localMS);
    }

    private VideoCapturer getVideoCapturer() {
        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
        String frontCameraDeviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
        if (numberOfCameras > 1 && frontCameraDeviceName != null)
            cameraDeviceName = frontCameraDeviceName;
        Log.d(TAG, "getVideoCapturer:" + cameraDeviceName);
        return VideoCapturerAndroid.create(cameraDeviceName);
    }

}
