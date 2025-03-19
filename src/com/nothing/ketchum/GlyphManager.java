package com.nothing.ketchum;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.nothing.ketchum.Glyph.Code_20111;
import com.nothing.ketchum.Glyph.Code_22111;
import com.nothing.ketchum.Glyph.Code_23111;
import com.nothing.ketchum.Glyph.Code_24111;
import com.nothing.thirdparty.IGlyphService;
import com.nothing.thirdparty.IGlyphService.Stub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlyphManager {
    private static final String TAG = "GlyphManager";
    private Context mContext;
    private RemoteServiceConnection mConnection = new RemoteServiceConnection();
    private IGlyphService mService;
    private Callback mCallback;
    private boolean mHasAuthorized = false;
    private ExecutorService mExecutor;
    private Future mTask = null;
    private String mDevice = null;
    private static GlyphManager mInstance = null;

    public static GlyphManager getInstance(Context context) {
        if(mInstance == null) mInstance = new GlyphManager(context);
        return mInstance;
    }

    private GlyphManager(Context context) {
        mContext = context;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void init(Callback callback) {
        mCallback = callback;
        Intent launchService = new Intent();
        launchService.setPackage("com.nothing.thirdparty");
        launchService.setAction("com.nothing.thirdparty.bind_glyphservice");
        launchService.setComponent(new ComponentName("com.nothing.thirdparty", "com.nothing.thirdparty.GlyphService"));
        mContext.bindService(launchService, mConnection, 1);
    }

    public void unInit() {
    	mContext.unbindService(mConnection);
    }

    public boolean register() {
    	if(Common.is20111()) mDevice = Glyph.DEVICE_20111;
    	else if(Common.is22111()) mDevice = Glyph.DEVICE_22111;
    	else if(Common.is23111()) mDevice = Glyph.DEVICE_23111;
    	else if(Common.is24111()) mDevice = Glyph.DEVICE_24111;
    	else mDevice = null;
    	
        try {
            String key = Common.getAppKey(mContext);
            mHasAuthorized = mService.registerSDK(key, mDevice);
        }
        catch (RemoteException var3) {
            Log.e(TAG, var3.getMessage());
        }
        catch (Exception var4) {
            Log.e(TAG, var4.getMessage(), var4);
        }

        Log.w(TAG, "You are targeting " + mDevice + " as your device.");
        return mHasAuthorized;
    }

    public GlyphFrame.Builder getGlyphFrameBuilder() {
        return this.mDevice == null ? null : new GlyphFrame.Builder(this.mDevice);
    }

    public void openSession() throws GlyphException {
        if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else if(mService == null) {
            throw new GlyphException("Please use it after service connected.");
        }
        else {
            try {
                mService.openSession();
            }
            catch (RemoteException var2) {
                Log.d(TAG, var2.getMessage());
            }

        }
    }

    public void closeSession() throws GlyphException {
        if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else if(mService == null) {
            throw new GlyphException("Please use it after service connected.");
        }
        else {
            try {
                stopCurrentTask();
                mService.closeSession();
            }
            catch (RemoteException var2) {
                Log.d(TAG, var2.getMessage());
            }

        }
    }

    public void setFrameColors(int[] colors) throws GlyphException {
        if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else if(mService == null) {
            throw new GlyphException("Please use it after service connected.");
        }
        else {
            try {
                stopCurrentTask();
                mService.setFrameColors(colors);
            }
            catch (RemoteException var3) {
                Log.e(TAG, var3.getMessage());
            }

        }
    }

    public void turnOff() {
        if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else {
            stopCurrentTask();
            
            Runnable task = new Runnable() {
                public void run() {
                	try {
                        GlyphFrame.Builder builder = getGlyphFrameBuilder();
                        GlyphFrame emptyFrame = builder.build();
                        mService.setFrameColors(emptyFrame.getChannel());
                    }
                    catch (RemoteException var3) {
                        Log.e(TAG, var3.getMessage());
                    }
                }
            };

            executeFrame(task);
        }
    }

    public void toggle(final GlyphFrame frame) {
        if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else {
            stopCurrentTask();
            
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        mService.setFrameColors(frame.getChannel());
                    }
                    catch (RemoteException var2) {
                        Log.e(TAG, var2.getMessage());
                    }

                }
            };
            
            executeFrame(task);
        }
    }
    
    public void animate(final GlyphFrame frame) {
    	if(!mHasAuthorized) Log.d(TAG, "Not registered");
        else {
        	String device = mDevice;
        	
            Runnable task = new Runnable() {
                public void run() {
                    int cycle = 0;
                    int period = frame.getPeriod();
                    int interval = frame.getInterval();

                    for(GlyphFrame emptyFrame = (new GlyphFrame.Builder(device)).build(); cycle < frame.getCycles(); ++cycle) {
                    	try {
                    		for(long startOn = System.currentTimeMillis(); System.currentTimeMillis() - startOn < (long)period; pauseAWhile(10L)) {
                                int[] colors = frame.getChannel();
                                int light;
                                if(System.currentTimeMillis() - startOn < (long)(period / 2)) {
                                    light = 0 + 4096 / (period / 2) * (int)(System.currentTimeMillis() - startOn);
                                }
                                else {
                                    light = 4096 - 4096 / (period / 2) * (int)(System.currentTimeMillis() - startOn - (long)(period / 2));
                                }

                                for(int i = 0; i < colors.length; ++i) {
                                    if(colors[i] != 0) colors[i] = light;
                                }

                                mService.setFrameColors(colors);
                        	}
                    	}
                    	catch (InterruptedException e) {
                            return;
                        }
                        catch (RemoteException var11) {
                            Log.e(TAG, var11.getMessage());
                        }
                    	
                    	try {
                        	for(long startOff = System.currentTimeMillis(); System.currentTimeMillis() - startOff < (long)interval; pauseAWhile(100L)) {
                                mService.setFrameColors(emptyFrame.getChannel());
                            }
                    	}
                    	catch (InterruptedException e) {
                            return;
                        }
                        catch (RemoteException var10) {
                            Log.e(TAG, var10.getMessage());
                        }
                    }

                }
            };

            stopCurrentTask();
            executeFrame(task);
        }
    	
    }
    
    public void displayAnimation(final List<GlyphFrame.Builder> animation, final long delayBetweenFrames, final boolean isIndefinite) {
    	if(!mHasAuthorized) Log.d(TAG, "Not registered");
    	else if(animation.isEmpty()) Log.d(TAG, "Empty animation");
    	else if(delayBetweenFrames <= 0) Log.d(TAG, "Invalid delayBetweenFrames value");
        else {
            Runnable task = new Runnable() {
                public void run() {
                	do {
                		for(int i = 0; i < animation.size(); i++) {
                    		try {
	                    		GlyphFrame.Builder frame = animation.get(i);
	                            mService.setFrameColors(frame.build(false).getChannel());
	                            pauseAWhile(delayBetweenFrames);
                    		}
                    		catch (InterruptedException e) {
                                return;
                            }
                            catch (RemoteException var2) {
                                Log.e(TAG, var2.getMessage());
                            }
                    	}
                	} while(isIndefinite);
                    turnOff();
                }
            };
            
            stopCurrentTask();
            executeFrame(task);
        }
    }
    
    private void pauseAWhile(long time) throws InterruptedException {
    	Thread.sleep(time);
    }
    
    private void applyFrameToBuilder(GlyphFrame frame, GlyphFrame.Builder builder, int intensity, List<Integer> excludedChannels) {
    	final int[] channel = frame.getChannel();
    	for(int ch = 0; ch < channel.length; ch++) {
    		if(channel[ch] > 0 && !excludedChannels.contains(ch)) builder.buildChannel(ch, intensity);
    	}
    }
     
    private static List<Integer> getVariableGlyphIndexes(int channel) {
        IntStream tmp = IntStream.empty();

        if(channel == 0) {
            if(Common.is20111()) tmp = IntStream.rangeClosed(Code_20111.D1_1, Code_20111.D1_8);
            else if(Common.is22111()) tmp = IntStream.rangeClosed(Code_22111.D1_1, Code_22111.D1_8);
            else if(Common.is23111()) tmp = IntStream.rangeClosed(Code_23111.C_1, Code_23111.C_24);
            else if(Common.is24111()) tmp = IntStream.rangeClosed(Code_24111.A_1, Code_24111.A_20);
        }
        else if (channel == 1) {
            if(Common.is22111()) tmp = IntStream.rangeClosed(Code_22111.C1_1, Code_22111.C1_16);
            else if(Common.is24111()) tmp = IntStream.rangeClosed(Code_24111.B_1, Code_24111.B_11);
        }
        else if(channel == 2) {
            if(Common.is24111()) tmp = IntStream.rangeClosed(Code_24111.C_1, Code_24111.C_5);
        }

        // Return an empty list if the stream is empty, otherwise collect to a list
        List<Integer> resultList = tmp.boxed().collect(Collectors.toList());
        return resultList.isEmpty() ? Collections.emptyList() : resultList;
    }
    
    private static List<Integer> reverseList(List<Integer> list) {
        List<Integer> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }
    
    private int getLight(GlyphFrame frame) throws GlyphException {
    	final int[] channel = frame.getChannel();
        
        if(Common.isDevice20111(mDevice)) {
            if(channel[Code_20111.D1_1] == 0) {
                throw new GlyphException("Please choose D1_1 while using display progress in 20111.");
            }

            return 0;
        }
        else if(Common.isDevice22111(mDevice)) {
            if(channel[Code_22111.C1_1] == 0 && channel[Code_22111.D1_1] == 0 || channel[Code_22111.C1_1] > 0 && channel[Code_22111.D1_1] > 0) {
                throw new GlyphException("Please choose C1_1 or D1_1 while using display progress in 22111.");
            }

            return channel[Code_22111.D1_1] > 0 ? 0 : 1;
        }
        else if(Common.isDevice23111(mDevice)) {
            if(channel[Code_23111.C_1] == 0) {
                throw new GlyphException("Please choose C_1 while using display progress in 23111.");
            }

            return 0;
        }
        else if(Common.isDevice24111(mDevice)) {
        	ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(channel[Code_24111.A_1]);
            arrayList.add(channel[Code_24111.B_1]);
            arrayList.add(channel[Code_24111.C_1]);
            
            int max = Collections.max(arrayList);
            int sum = arrayList.stream().mapToInt(Integer::intValue).sum();
        	
            if(sum == 0 || sum/max != 1) {
                throw new GlyphException("Please choose A_1, B_1 or C_1 while using display progress in 24111.");
            }

            if(channel[Code_24111.A_1] != 0) return 0;
            else if(channel[Code_24111.B_1] != 0) return 1;
            else return 2;
        }
        else return -1;
    }
    
    private void displayVariableAnimation(
    		boolean isRegressive,
    		GlyphFrame frame,
    		int progress,
    		int intensity,
    		int duration,
    		int stepSize,
    		long durationAfterAnimation,
    		boolean isReverse,
    		boolean isToggle) throws GlyphException {
    	if(!mHasAuthorized) {
    		Log.d(TAG, "Not registered");
    		return;
    	}
    	
    	GlyphFrame.Builder staticBuilder;
        try {
        	staticBuilder = new GlyphFrame.Builder(mDevice);
        }
        catch (NullPointerException e) {
        	throw new GlyphException("Could not create GlyphFrame.Builder");
        }
        
        int light = getLight(frame);
        List<Integer> variableGlyphIndexes = getVariableGlyphIndexes(light);
        if(variableGlyphIndexes.isEmpty() || intensity <= 0 || progress <= 0 || duration <= 0 || duration < stepSize) {
            return;
        }
        
        int sanitizedIntensity = Math.min(intensity, Common.MAX_GLYPH_INTENSITY - 1);
        int sanitizedProgress = Math.min(progress, 100);
        int sanitizedStepSize = Math.max(stepSize, 1);

        int totalFrames = duration / sanitizedStepSize;
        int glyphIndexesSize = variableGlyphIndexes.size();
        if(glyphIndexesSize == 0) return;
        int numFramesPerSubzone = totalFrames / glyphIndexesSize;
        if(numFramesPerSubzone == 0) return;

        int intensityVariationPerFrame = sanitizedIntensity / numFramesPerSubzone;
        if(intensityVariationPerFrame == 0) return;

        double progressPerSubzone = 100.0 / glyphIndexesSize;
        double highestReachableSubzoneIndexDouble = sanitizedProgress / progressPerSubzone;
        int highestReachableSubzoneIndex = (int) Math.ceil(highestReachableSubzoneIndexDouble);
        double fractionalPart = highestReachableSubzoneIndexDouble - (int) Math.floor(highestReachableSubzoneIndexDouble);
        int numFramesLastSubzone = (fractionalPart == 0.0) ? numFramesPerSubzone : (int) (numFramesPerSubzone * fractionalPart);
        
        final List<Integer> indexes;
        int startIndex = glyphIndexesSize - highestReachableSubzoneIndex;
        int endIndex = highestReachableSubzoneIndex;
        
        if(!isReverse) {
        	if(isRegressive) indexes = reverseList(variableGlyphIndexes.subList(0, endIndex));
        	else indexes = variableGlyphIndexes.subList(0, endIndex);
        }
        else {
        	if(!isRegressive) indexes = reverseList(variableGlyphIndexes.subList(startIndex, glyphIndexesSize));
        	else indexes = variableGlyphIndexes.subList(startIndex, glyphIndexesSize);
        }
        
        Runnable task = () -> {
            try {
            	GlyphFrame.Builder dynamicBuilder = new GlyphFrame.Builder(staticBuilder);
            	
            	if(isRegressive) {
            		for(int index: indexes) {
                    	dynamicBuilder.buildChannel(index, sanitizedIntensity);
                    }
            	}
            	
            	int lastNextIntensityValue = isRegressive ? numFramesLastSubzone * intensityVariationPerFrame : 0;
            	int highestReachableSubzone = indexes.get(endIndex - 1);
            	
            	// in case of very short step size show the progress immediately
            	int startingIndex = 0;
            	if(sanitizedStepSize == 1) startingIndex = indexes.size() - 1;
            	int glyphIndex = indexes.get(startingIndex);
            	
                for(int index = startingIndex; index < indexes.size(); index++) {
                	glyphIndex = indexes.get(index);
                	
                    int currentIntensity = lastNextIntensityValue;
                    int numFrames = numFramesPerSubzone;
                    if(glyphIndex == highestReachableSubzone) {
                    	currentIntensity = 0;
                    	numFrames = numFramesLastSubzone;
                    }
                    
                    for(int j = indexes.get(0); j < glyphIndex; j++) {
                    	int finalIntensity = 0;
                    	if(!isRegressive) finalIntensity = sanitizedIntensity;
                    	dynamicBuilder.buildChannel(j, finalIntensity);
                    }
                    
                    if(numFrames == 0) {
                    	if(isToggle) applyFrameToBuilder(frame, dynamicBuilder, intensity, variableGlyphIndexes);
                    	int[] var = dynamicBuilder.build(false).getChannel();
                    	mService.setFrameColors(var);
                        pauseAWhile(sanitizedStepSize);
                    }
                    else {
                    	for(int j = 1; j <= numFrames; j++) {
                            if(isRegressive) {
                            	if(j == numFrames) currentIntensity = 0;
                            	else currentIntensity = Math.max(currentIntensity - intensityVariationPerFrame, 0);
                            }
                            else {
                            	currentIntensity = Math.min(currentIntensity + intensityVariationPerFrame, sanitizedIntensity);
                            }

                            dynamicBuilder.buildChannel(glyphIndex, currentIntensity);

                            if(isRegressive) lastNextIntensityValue = Math.max(sanitizedIntensity - sanitizedIntensity * j / (4 * numFrames), 0);
                            else lastNextIntensityValue = currentIntensity/8;
                            
                            if(isReverse != isRegressive && glyphIndex > highestReachableSubzone - 1) {
                                dynamicBuilder.buildChannel(glyphIndex - 1, lastNextIntensityValue);
                            }
                            else if(isReverse == isRegressive && glyphIndex < highestReachableSubzone - 1) {
                                dynamicBuilder.buildChannel(glyphIndex + 1, lastNextIntensityValue);
                            }
                            
                            if(isToggle) applyFrameToBuilder(frame, dynamicBuilder, intensity, variableGlyphIndexes);

                            // using false for build to recycle frames, this avoids having to set all channels to 'intensity' value every cycle
                            int[] var = dynamicBuilder.build(false).getChannel();
                            mService.setFrameColors(var);
                            pauseAWhile(sanitizedStepSize);
                        }
                    }
                }
            }
            catch(InterruptedException e) {
                return;
            }
            catch(RemoteException var12) {
                Log.e(TAG, var12.getMessage());
            }
            
            
            if(durationAfterAnimation == 0) turnOff();
        	else if(durationAfterAnimation > 0) {
            	try {
					pauseAWhile(durationAfterAnimation);
				}
            	catch (InterruptedException e) {
            		return;
            	}
            	turnOff();
        	}
        };

        stopCurrentTask();
        executeFrame(task);
    }
    
    public void displayProgress(GlyphFrame frame, int progressValue, int intensity, int duration, int stepSize, int durationAfterAnimation, boolean isReverse, boolean isToggle) throws GlyphException {
    	displayVariableAnimation(false, frame, progressValue, intensity, duration, stepSize, durationAfterAnimation, isReverse, isToggle);
    }
    
    public void displayDefaultProgress(GlyphFrame frame, int progressValue, int intensity) throws GlyphException {
    	displayVariableAnimation(false, frame, progressValue, intensity, 1000, 25, 0, false, false);
    }
    
    public void displayDefaultProgressAndToggle(GlyphFrame frame, int progressValue, int intensity) throws GlyphException {
    	displayVariableAnimation(false, frame, progressValue, intensity, 1000, 25, 0, false, true);
    }
    
    public void displayRegress(GlyphFrame frame, int regressionValue, int intensity, int duration, int stepSize, int durationAfterAnimation, boolean isReverse, boolean isToggle) throws GlyphException {
    	displayVariableAnimation(true, frame, regressionValue, intensity, duration, stepSize, durationAfterAnimation, isReverse, isToggle);
    }

    public void displayDefaultRegress(GlyphFrame frame, int progressValue, int intensity) throws GlyphException {
    	displayVariableAnimation(false, frame, progressValue, intensity, 1000, 25, 0, false, false);
    }
    
    public void displayDefaultRegressAndToggle(GlyphFrame frame, int progressValue, int intensity) throws GlyphException {
    	displayVariableAnimation(false, frame, progressValue, intensity, 1000, 25, 0, false, true);
    }
    
    private void executeFrame(Runnable task) {
    	stopCurrentTask();
        mTask = mExecutor.submit(task);
    }

    private void stopCurrentTask() {
        if(mTask != null) {
        	mTask.cancel(true);
        	mTask = null;
        }
    }

    public interface Callback {
        void onServiceConnected(ComponentName var1);

        void onServiceDisconnected(ComponentName var1);
    }

    private class RemoteServiceConnection implements ServiceConnection {
        private RemoteServiceConnection() {}

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service connected");
            mService = Stub.asInterface(service);
            if(mCallback != null) mCallback.onServiceConnected(name);
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service disconnected");
            mService = null;
            if(mCallback != null) mCallback.onServiceDisconnected(name);
        }
    }
}
