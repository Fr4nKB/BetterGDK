package com.nothing.ketchum;

import com.nothing.ketchum.Glyph.Code_20111;
import com.nothing.ketchum.Glyph.Code_22111;
import com.nothing.ketchum.Glyph.Code_23111;
import com.nothing.ketchum.Glyph.Code_24111;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GlyphFrame {
    private ArrayList<Integer> mChannel = new ArrayList<Integer>();
    private int mPeriod = 0;
    private int mCycles = 1;
    private int mInterval = 0;

    private GlyphFrame(Builder builder) {
        this.mPeriod = builder.period;
        this.mCycles = builder.cycles;
        this.mInterval = builder.interval;
        this.mChannel = builder.channel;
    }

    public int getPeriod() {
        return this.mPeriod;
    }

    public int getCycles() {
        return this.mCycles;
    }

    public int getInterval() {
        return this.mInterval;
    }

    public int[] getChannel() {
        int[] result = new int[this.mChannel.size()];

        for(int i = 0; i < this.mChannel.size(); ++i) {
            result[i] = this.mChannel.get(i) != null ? (Integer)this.mChannel.get(i) : 0;
        }

        return result;
    }

    public static class Builder {
        private int period = 0;
        private int cycles = 1;
        private int interval = 0;
        private ArrayList<Integer> channel;
        private String mDevice;
        private int size = 0;

        public Builder(String targetDevice) {
        	if(Common.is20111()) {
        		this.size = Glyph.DEVICE_20111_SIZE;
                this.mDevice = Glyph.DEVICE_20111;
        	}
        	else if(Common.is22111()) {
        		this.size = Glyph.DEVICE_22111_SIZE;
                this.mDevice = Glyph.DEVICE_22111;
        	}
        	else if(Common.is23111()) {
        		this.size = Glyph.DEVICE_23111_SIZE;
                this.mDevice = Glyph.DEVICE_23111;
        	}
        	else {
        		this.size = Glyph.DEVICE_24111_SIZE;
                this.mDevice = Glyph.DEVICE_24111;
        	}
        	
        	this.channel = new ArrayList<Integer>(Collections.nCopies(this.size, 0));
        }
        
        public Builder(Builder builder) {
        	this.period = builder.period;
        	this.cycles = builder.cycles;
        	this.interval = builder.interval;
        	this.channel = new ArrayList<>(builder.channel); 
        	this.mDevice = builder.mDevice;
        	this.size = builder.size;
        }

        public Builder buildPeriod(int period) {
        	if(period < 0) return this;
            this.period = period;
            return this;
        }

        public Builder buildCycles(int cycles) {
        	if(cycles < 0) return this;
            this.cycles = cycles;
            return this;
        }

        public Builder buildInterval(int interval) {
        	if(interval < 0) return this;
            this.interval = interval;
            return this;
        }

        public Builder buildChannel(int channel) {
        	if(channel < 0) return this;
            this.channel.set(channel, Common.DEFAULT_GLYPH_INTENSITY);
            return this;
        }

        public Builder buildChannel(int channel, int lightIntensity) {
        	if(lightIntensity < 0 || lightIntensity >= Common.MAX_GLYPH_INTENSITY)
        		lightIntensity = Common.DEFAULT_GLYPH_INTENSITY;
        	this.channel.set(channel, lightIntensity);
            return this;
        }
        
        private void setChannelValues(int startCode, int endCode, int lightIntensity) {
            for(int code = startCode; code <= endCode; code++) {
                this.channel.set(code, lightIntensity);
            }
        }
        
        private int sanitizeLightIntensity(int glyphLightIntensity) {
        	if(glyphLightIntensity < 0) glyphLightIntensity = 0;
        	else if(glyphLightIntensity >= Common.MAX_GLYPH_INTENSITY)
        		glyphLightIntensity = Common.DEFAULT_GLYPH_INTENSITY;
        	
        	return glyphLightIntensity;
        }
        
        private Builder setChannelA(int glyphLightIntensity) {
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
        	
            if(Common.isDevice20111(this.mDevice)) this.channel.set(Code_20111.A1, glyphLightIntensity);
            else if(Common.isDevice22111(this.mDevice)) {
                this.channel.set(Code_22111.A1, glyphLightIntensity);
                this.channel.set(Code_22111.A2, glyphLightIntensity);
            }
            else if(Common.isDevice23111(this.mDevice)) this.channel.set(Code_23111.A, glyphLightIntensity);
            else if(Common.isDevice24111(this.mDevice)) setChannelValues(Code_24111.A_1, Code_24111.A_11, glyphLightIntensity);

            return this;
        }

        public Builder buildChannelA(int glyphLightIntensity) {
        	return setChannelA(sanitizeLightIntensity(glyphLightIntensity));
        }
        
        public Builder buildChannelA() {
        	return setChannelA(Common.DEFAULT_GLYPH_INTENSITY);
        }
        

        private Builder setChannelB(int glyphLightIntensity) {      
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
        	
            if(Common.isDevice20111(this.mDevice))this.channel.set(Code_20111.B1, glyphLightIntensity);
            else if(Common.isDevice22111(this.mDevice)) this.channel.set(Code_22111.B1, glyphLightIntensity);
            else if(Common.isDevice23111(this.mDevice)) this.channel.set(Code_23111.B, glyphLightIntensity);
            else if(Common.isDevice24111(this.mDevice)) setChannelValues(Code_24111.B_1, Code_24111.B_5, glyphLightIntensity);

            return this;
        }
        
        public Builder buildChannelB(int glyphLightIntensity) {
        	return setChannelB(glyphLightIntensity);
        }
        
        public Builder buildChannelB() {
        	return setChannelB(Common.DEFAULT_GLYPH_INTENSITY);
        }
        

        private Builder setChannelC(int glyphLightIntensity) {        
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
        	
            if(Common.isDevice20111(this.mDevice)) setChannelValues(Code_20111.C1, Code_20111.C4, glyphLightIntensity);
            else if(Common.isDevice22111(this.mDevice)) setChannelValues(Code_22111.C1_1, Code_22111.C6, glyphLightIntensity);
            else if(Common.isDevice23111(this.mDevice)) setChannelValues(Code_23111.C_1, Code_23111.C_24, glyphLightIntensity);
            else if(Common.isDevice24111(this.mDevice)) setChannelValues(Code_24111.C_1, Code_24111.C_20, glyphLightIntensity);

            return this;
        }
        
        public Builder buildChannelC(int glyphLightIntensity) {
        	return setChannelC(glyphLightIntensity);
        }
        
        public Builder buildChannelC() {
        	return setChannelC(Common.DEFAULT_GLYPH_INTENSITY);
        }
        
        
        private Builder setChannelC1(int glyphLightIntensity) {
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
        	
        	if(Common.isDevice22111(this.mDevice)) setChannelValues(Code_22111.C1_1, Code_22111.C1_16, glyphLightIntensity);
            return this;
        }
        
        public Builder buildChannelC1(int glyphLightIntensity) {
        	return setChannelC1(glyphLightIntensity);
        }
        
        public Builder buildChannelC1() {
        	return setChannelC1(Common.DEFAULT_GLYPH_INTENSITY);
        }
        

        private Builder setChannelD(int glyphLightIntensity) {
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
            if(Common.isDevice20111(this.mDevice)) setChannelValues(Code_20111.D1_1, Code_20111.D1_8, glyphLightIntensity);
            else if(Common.isDevice22111(this.mDevice)) setChannelValues(Code_22111.D1_1, Code_22111.D1_8, glyphLightIntensity);
            return this;
        }
        
        public Builder buildChannelD(int glyphLightIntensity) {
        	return setChannelD(glyphLightIntensity);
        }
        
        public Builder buildChannelD() {
        	return setChannelD(Common.DEFAULT_GLYPH_INTENSITY);
        }
        

        private Builder setChannelE(int glyphLightIntensity) {
        	glyphLightIntensity = sanitizeLightIntensity(glyphLightIntensity);
            if(Common.isDevice20111(this.mDevice))this.channel.set(Code_20111.E1, glyphLightIntensity);
            else if(Common.isDevice22111(this.mDevice))this.channel.set(Code_22111.E1, glyphLightIntensity);

            return this;
        }
        
        public Builder buildChannelE(int lightIntensity) {
        	return setChannelE(lightIntensity);
        }
        
        public Builder buildChannelE() {
        	return setChannelE(Common.DEFAULT_GLYPH_INTENSITY);
        }
        
        public Builder mergeFrame(Builder frame) {
        	if(this.size != frame.size) return this;
        	for(int i = 0; i < size; i++) {
        		int newChVal = this.channel.get(i) + frame.channel.get(i);
        		if(newChVal >= Common.MAX_GLYPH_INTENSITY) newChVal = Common.MAX_GLYPH_INTENSITY - 1;
        		this.channel.set(i, newChVal);
        	}
        	
        	return this;
        }
        
        public GlyphFrame build(boolean reset) {
        	GlyphFrame frame = new GlyphFrame(this);
        	
        	// reset the frame back to default, in this way we don't need to create a new frame
        	// to toggle off the previous one
        	if(reset) {
            	this.period = 0;
            	this.cycles = 1;
            	this.interval = 0;
            	this.channel = new ArrayList<Integer>(Collections.nCopies(size, 0));
        	}
            return frame;
        }

        public GlyphFrame build() {
        	return build(true);
        }
    }
}

