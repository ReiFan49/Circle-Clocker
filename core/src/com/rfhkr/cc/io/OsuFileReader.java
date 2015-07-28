package com.rfhkr.cc.io;

import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.cc.level.Chart.*;
import com.rfhkr.cc.level.convert.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/10
 */
public class OsuFileReader implements FileFormatReader<OsuFileReader> {
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
	public OsuFileReader parse(@NotNull File file) {
		try (BufferedReader f = new BufferedReader(new FileReader(file))) {
			String str; Object obj = null;
			RAWOsuBeatmap bm = new RAWOsuBeatmap();
			Matcher lm,mt;
			OsuTags tags = null;
			if (!Objects.nonNull(str = f.readLine()) ||
					!(lm = Pattern.compile("osu file format v(\\d+)").matcher(str)).matches()) {
				throw ReiException.invoke("Invalid osu! beatmap file.");
			} else {
				System.out.printf("Parsing osu file format v%s%n",lm.group(1));
			}
			bm.cp = file.getPath();
			while(Objects.nonNull(str = f.readLine()) /** EOF MARKER */) {
				str = str.replaceAll("\n|\r\n?","");
				if(Pattern.compile("^$").matcher(str).matches()) continue;
				if((lm = Pattern.compile("^\\[(\\w+)\\]$").matcher(str)).matches()) {
					tags = OsuTags.valueOf(lm.group(1));
					continue;
				} else if ((lm = Pattern.compile("^(\\w+):\\s?(.+)$").matcher(str)).matches()) {
					obj = lm.group(2);
					if((mt = Pattern.compile("\\-?\\d+(\\.\\d+)?").matcher((String)obj)).matches())
						switch(mt.start(1)) {
							case -1: obj = Integer.parseInt((String)obj); break;
							default: obj = Float.parseFloat((String)obj); break;
						}
					//else
					//	System.out.println(str);
					obj = obj.getClass().cast(obj);
				} else if ((lm = Pattern.compile("^//(.+)").matcher(str)).matches()) {
					if(lm.group(1).contains("Background and Video events"))
						try {
							mt = Pattern.compile("\"(.+)\"").matcher(
								str = Pattern.compile(",").split(f.readLine())[2]
							);
							obj = mt.matches() ? mt.group(1) : str;
						} catch (Exception e) {
							System.err.println("No background detected.");
							continue;
						}
					else
						continue;
				} else if(tags == OsuTags.TimingPoints) {
					String[] astr; Object[] aobj;
					astr = Pattern.compile(",").split(str);
					aobj = new Object[astr.length];
					aobj[0] = Float.valueOf(astr[0])/1000.0f;  // OFFSET
					aobj[1] = 60000/Double.valueOf(astr[1]);   // BPM
					aobj[2] = Byte.valueOf(astr[2]);           // BAR
					aobj[3] = Byte.valueOf(astr[3]);           // SAMPLE TYPE
					aobj[4] = Byte.valueOf(astr[4]);           // CUSTOM SAMPLE
					aobj[5] = Byte.valueOf(astr[5]);           // VOLUME
					aobj[6] = Byte.valueOf(astr[6])!=0;        // TIMING TYPE
					aobj[7] = Byte.valueOf(astr[7]);           // SPECIAL TAGS
					if(!(boolean)aobj[6]) continue;          /** SKIP INHERITED */
					if(!bm.timing.haveTiming(Timing.at(0))) {
						bm.timing
							.addTiming(BPMData.on((double)aobj[1],(byte)aobj[2]))
							.setFirstOffset((float)aobj[0]);
					} else {
						bm.timing
							.addTiming(
								bm.timing.approx((float) aobj[0]),
								BPMData.on((double) aobj[1],(byte) aobj[2])
							);
					}
					continue;
				} else if (tags == OsuTags.HitObjects) {
					String[] astr;
					Object[] aobj;
					boolean  needMapping = bm.posMap.isEmpty(),
						keepLooping = bm.posMap.isEmpty();
					float    cueTime = 0.0f,curTime;
					do {
						astr = Pattern.compile(",").split(str);
						aobj = new Object[astr.length];
						aobj[0] = Integer.valueOf(astr[0]);
						aobj[1] = Integer.valueOf(astr[1]);
						aobj[2] = curTime = Float.valueOf(astr[2]) / 1000.0f;
						aobj[3] = Short.valueOf(astr[3]);
						aobj[4] = Byte.valueOf(astr[4]);
						if(needMapping) {
							if(bm.posMap.isEmpty())
								cueTime = curTime;
							else
								if(cueTime != curTime)
									break;
							bm.posMap.add(Twin.set((int) aobj[0],(int) aobj[1]));
							if (Objects.isNull(str = f.readLine()))
								break;
						} else { break; }
					} while (true);
					try {
						final
							Timing start = bm.timing.approx((float)aobj[2]);
						final
							byte   pos   = (byte)(bm.posMap.indexOf(Twin.set((int)aobj[0],(int)aobj[1]))+1);
						Timing end   = start;
						if(pos==0)
							throw ReiException.invoke();
						switch((short)aobj[3] & 0b1000_1011) {
							/** == Tips on handling note types
							 *  0b0000_0001, NORMAL
							 *  osu! only handles 5 main object parameter.
							 *  0b0000_0010, SLIDER
							 *  osu! handles extra 3 parameter to attain slider length
							 *  which affects end time of the object
							 *  0b0000_1000, SPINNER
							 *  osu! handles ONE extra parameter which affects spinner length
							 *  0b1000_0000, MANIA HOLD
							 *  osu! handles ONE extra parameter which injected to the extra parameter....
							 *  1,2,3,4,5,6:_:_:_:_:_ instead 1,2,3,4,5,6,_:_:_:_:_
							 *  (treat colon as comma, and kidnap the 6th parameter)
							 */
							case 0b0000_0001: // NORMAL NOTE
									bm.note.add(new Note(NoteType.NOTE_NORM,pos,start,end));
								break;
							case 0b0000_0010: // HOLD NOTE
								String[] asld = Pattern.compile("\\|").split(astr[5]);
								aobj[5] = asld[0].charAt(0);
								if((char)aobj[5] != 'L')
									switch(asld.length) {
										case 3: // 2 point slider, linear only
											break;
										case 4: // 3 point slider
											break;
										default:
											throw ReiException.invoke("Bad slider");
									}
								aobj[6] = Short.valueOf(astr[6]);
								aobj[7] = Double.valueOf(astr[7]);
								double alen = (short)aobj[6] * (double)aobj[7] / (Float.valueOf(bm.diff.get("SliderMultiplier").toString())*100);
								end = Timing.shift(start,Timing.valueOf(alen));
								bm.note.add(new Note(NoteType.NOTE_LONG,pos,start,end));
								break;
							case 0b0000_1000: // DEPRECATED SPINNER
							case 0b1000_0000: // DEPRECATED HOLD
								throw ReiException.invoke("Attempting to convert unsupported osu! note type.");
						}
					} catch(Exception e) {
						System.err.printf("%s: %s%nRaw Object: %s%n",e.getClass(),e.getMessage(),str);
					}
				} else {
					continue;
				}
				if(tags==null) continue;
				switch(tags) {
					case General:
						bm.general.put(lm.group(1),obj);
						break;
					case Metadata:
						bm.meta.put(lm.group(1),obj.toString());
						break;
					case Difficulty:
						bm.diff.put(lm.group(1),obj);
						break;
					case Events:
						bm.bg = obj.toString();
						break;
					default:
						break;
				}
			}
			//bm.posMap.forEach(System.out::println);
			//System.out.println(bm.timing.getFirstOffset() + "sec");
		//bm.note.forEach((x) -> System.out.printf("{%s} %s -> %s (x%s)%n",x.getPos(),x.getStart(),x.getEnd(),x.getAmp()));
			bm.convert();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	// <<END>> Instance Structure
	// Nested Class
	private static class RAWOsuBeatmap implements Convertable {
		public final Map<String,Object> general = new TreeMap<>();
		public final Map<String,String> meta = new TreeMap<>();
		public final Map<String,Object> diff = new TreeMap<>();
		public String bg;
		public String cp;
		public final Timingset timing = new Timingset();
		public final List<Twin<Integer>> posMap = new LinkedList<>();
		public final Set<Note> note = new TreeSet<>(Note::compareTo);
		public Chart convert() {
			String[] tags = Pattern.compile("\\s+").split(meta.get("Tags"));
			Matcher lm;
			Pair<String,Boolean> diffMode = Pair.gen("",false);
			Pair<DiffType,Byte> diffType = Pair.gen(null,null);
			/** Header check **/
			if(!tags[0].equalsIgnoreCase("CirClockChart"))
				throw new RuntimeException(ReiException.invoke("CirClockChart tag header must appear first before other " +
						"tags\nto ensure the beatmap is served for this game."));
			/** Difficulty Name Check **/
			if (!(lm = Pattern.compile("(\\w*)(08|16)\\z").matcher(tags[1])).matches())
				throw new RuntimeException(ReiException.invoke("Difficulty tag does not follow the base header (XXXXXXYY)"));
			diffMode.setBoth(lm.group(1),lm.group(2).equals("16"));
			/** Difficulty ID Check **/
			if (!(lm = Pattern.compile("LEVEL([0-4][0-9])\\z").matcher(tags[2])).matches())
				throw new IllegalStateException(ReiException.invoke("Cannot identify difficulty level"));
			diffType.setBoth(DiffType.determine(diffMode.get1st()),Byte.parseByte(lm.group(1)));
			/** Assign data to chart **/
			Chart ch = new Chart(diffMode.get1st(),meta.get("Creator"),diffType.get2nd());
			if(diffMode.get2nd()) /** CHECKS WHETHER 16 OR 08 MODE **/
				ch.switchMode();
			ch.chart.addAll(note.toArray(new Note[note.size()]));
			/** Assign data to metadata **/
			Metadata metadata = (new Metadata())
				.setTitle(meta.get("Title"),meta.getOrDefault("TitleUnicode",null))
				.setComposer(meta.get("Artist"),meta.getOrDefault("ArtistUnicode",null))
				.setSeries(meta.get("Source"))
				.setTimingSet(timing);
			/** Assign data to chartset **/
			Chartset cs = Chartset.designate(metadata,ch)
				.setSongName((String)general.get("AudioFilename"))
				.setSongBG(bg)
				.setPath(PathResolver.from(cp).rep(""));
			return ch;
		}
	}
	private enum OsuTags { General, Editor, Metadata, Difficulty, Events, Colours, TimingPoints, HitObjects }
	// Constructors
	// Driver
	public static void main(String... argv) {
		OsuFileReader pars = new OsuFileReader();
		PathResolver  nova = PathResolver.at("Hiro - VERTeX (Rei Hakurei) [Sample 08].osu")
			.build("resources","Charts","Hiro (maimai) - VERTeX");
		/** use arpg style to retain any files that next to the designated <nova> file */
		@NotNull
		Path          arpg = (new File(nova.resolve())).toPath().getParent().toAbsolutePath();
		pars.parse(nova);
		Chartset.cache.forEach(System.out::println);
		try {
			Chartset.detect("resources\\Charts");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Chartset.cache.forEach(System.out::println);
	}
}
