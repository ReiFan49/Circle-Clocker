package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.graphics.g2d.*;
import com.sun.istack.internal.*;
import com.rfhkr.cc.level.Chart.NoteType;
import static com.rfhkr.cc.level.Chart.NoteType.*;

import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/17
 */
class NoteLong extends NoteBasic implements EarlyJudgable,LateJudgable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	public NoteType getType() { return NOTE_LONG; }
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public void draw(final SpriteBatch batch) {
		float
			fTime[]  = new float[]{time_s,time_e},
			fOffX[]  = new float[]{0,0,31},
			limhgh   = fTime[1] + Judgement.MISS.minTime,
			drawA    = (float)Math.toDegrees(Math.atan2(desigPos.getX().y-pos.y,desigPos.getX().x-pos.x));
		Function<Number,Float>
			fLow  = (x) -> fTime[x.intValue()] - approach,
			fMed  = (x) -> fTime[x.intValue()],
			// limlen(gth)   = time required from center to note position
			fLen  = (x) -> fMed.apply(x) - fLow.apply(x),
			// limpro(gress) = percentage time along limlength
			fPro  = (x) ->
				(Gameplay.now().getElapsed() - fLow.apply(x)) / (fLen.apply(x)),
			fPro2 = (x) ->
				(x.intValue()==0) ? Math.min(Math.max(1,fPro.apply(1)),fPro.apply(0)) : fPro.apply(x),
			fScal = (x) ->
				fPro2.apply(x.intValue()) * 2,
			// lim positioning
			fApp  = (x) -> pcOFFSET + Math.max(0,fPro2.apply(x) - 0.5f) * (2.0f - pcOFFSET * 2),
			fXs   = (x) ->
				pos.x + (desigPos.getX().x - pos.x) * fApp.apply(x.intValue() >> 1) - fOffX[x.intValue()],
			fYs   = (x) ->
				pos.y + (desigPos.getX().y - pos.y) * fApp.apply(x.intValue() >> 1) - 32,
			fX    = (x) ->
				(x.intValue()==1) ?
					fXs.apply(2) + 29 + ((fPro2.apply(0)>=0.5f)?2:0) :
					fXs.apply(x),
			fY    = (x) -> (x.intValue()==1) ? fYs.apply(2) : fYs.apply(x);
		// only draw this if
		double d = fApp.apply(0) * Math.hypot(pos.x - desigPos.getX().x,pos.y - desigPos.getX().y);
		if(fPro2.apply(0) >= 0.0f && d <= 288) {
			if(fPro2.apply(0) < 0.5f) {
				/** Appearance process */
				batch.setColor(1,1,1,fScal.apply(0));
				batch.draw(noteSprite.get(0),fX.apply(0),fY.apply(0), 0,32,31,64,fScal.apply(0),fScal.apply(0),drawA,0,0,31,64,false,true);
				//batch.draw(noteSprite.get(1),fX.apply(1),fY.apply(1), 1,32, 2,64,fScal.apply(0),fScal.apply(0),drawA,0,0, 2,64,false,true);
				batch.draw(noteSprite.get(2),fX.apply(2),fY.apply(2),31,32,31,64,fScal.apply(0),fScal.apply(0),drawA,0,0,31,64,false,true);
				batch.setColor(1,1,1,1);
			} else {
				/** Movement process */
				float limprx;
				limprx = 0;
				if(fPro2.apply(0) > fPro2.apply(1))
					limprx = ((fPro2.apply(0)-0.5f)*2 - Math.max(0,fPro2.apply(1)-0.5f)*2);
				batch.draw(noteSprite.get(0),fX.apply(0),fY.apply(0), 0,32,31,64,1,1,drawA,0,0,31,64,false,true);
				batch.draw(noteSprite.get(1),fX.apply(1),fY.apply(1), 0,32,Math.max(2,240*(1-pcOFFSET)*limprx),64,1,1,drawA,0,0,noteSprite.get(1).getWidth(),64,false,true);
				batch.draw(noteSprite.get(2),fX.apply(2),fY.apply(2),31,32,31,64,1,1,drawA,0,0,31,64,false,true);
			}
		}
	}
	public void update() {

	}
	public void dispose() {}
	public void initializeNoteTexture() {
		this.noteSprite.addAll(getTripleTexture());
	}
	// <<END>> Instance Structure
	// Constructors
	// -- Simple Constructor (no Amplifier)
	public NoteLong(int slot,float s,float e) { this(slot,s,e,1); }
	// -- Grand Constructor (with Amplifier)
	public NoteLong(int slot,float s,float e,@NotNull int n) {
		super(slot,s,e,n);
	}
	// Driver
}
