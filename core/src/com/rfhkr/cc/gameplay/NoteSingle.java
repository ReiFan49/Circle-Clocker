package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.graphics.g2d.*;
import com.sun.istack.internal.*;

import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/17
 */
class NoteSingle extends NoteBasic implements LateJudgable {
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
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public void draw(final SpriteBatch batch) {
		float
			fTime[]  = new float[]{time_s},
			limhgh   = fTime[0] + Judgement.MISS.minTime,
			drawA    = (float)Math.toDegrees(Math.atan2(desigPos.getX().y-pos.y,desigPos.getX().x-pos.x));
		Supplier<Float>
			fLow  = () -> fTime[0] - approach,
			fMed  = () -> fTime[0],
			// limlen(gth)   = time required from center to note position
			fLen  = () -> fMed.get() - fLow.get(),
			// limpro(gress) = percentage time along limlength
			fPro  = () ->
				(Gameplay.now().getElapsed() - fLow.get()) / (fLen.get()),
			fScal = () ->
				fPro.get() * 2,
			// lim positioning
			fApp  = () -> pcOFFSET + Math.max(0,fPro.get() - 0.5f) * (2.0f - pcOFFSET * 2),
			fX    = () ->
				pos.x + (desigPos.getX().x - pos.x) * fApp.get() - 32,
			fY    = () ->
				pos.y + (desigPos.getX().y - pos.y) * fApp.get() - 32;
		// only draw this if
		if(fPro.get() >= 0.0f && Gameplay.now().getElapsed() < limhgh) {
			if(fPro.get() < 0.5f) {
				/** Appearance process */
				batch.setColor(1,1,1,fScal.get());
				batch.draw(noteSprite.get(0),fX.get(),fY.get(),32,32,64,64,fScal.get(),fScal.get(),drawA,0,0,64,64,false,true);
				batch.setColor(1,1,1,1);
			} else {
				/** Movement process */
				batch.draw(noteSprite.get(0),fX.get(),fY.get(),32,32,64,64,1,1,drawA,0,0,64,64,false,true);
			}
		}
	}
	public void update() {

	}
	public void dispose() {}
	public void initializeNoteTexture() {
		this.noteSprite.addAll(getSingleTexture());
	}
	// <<END>> Instance Structure
	// Constructors
	// -- Simple Constructor (no Amplifier)
	public NoteSingle(int slot,float s) { this(slot,s,1); }
	// -- Grand Constructor (with Amplifier)
	public NoteSingle(int slot,float s,@NotNull int n) {
		super(slot,s,s,n);
	}
	// Driver
}
