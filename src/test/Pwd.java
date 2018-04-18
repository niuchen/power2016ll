import java.util.Arrays;
import java.util.List;

import com.google.common.base.Splitter;
import com.power.common.utils.Encodes;
import com.power.common.utils.Global;
import com.power.common.utils.IdGen;
import com.power.common.utils.UploadUtils;
import com.power.common.utils.encrypt.Digests;


/**
 * 项目名称：power2016 <br>
 * 类名称：Pwd <br>
 * 创建时间：2016-10-31 下午7:03:06 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class Pwd {
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	public static void main(String[] args) {
//		String str = "123";
//		String plain = Encodes.unescapeHtml(str);
//		byte[] salt = Digests.generateSalt(SALT_SIZE);
//		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
//		String p = Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
//		System.out.println(p);
//		System.out.println(p.length());
//		
//		System.out.println(IdGen.uuid());
		
//		List<String> extList = Splitter.on(",").trimResults().splitToList(Global.getConfig("im.file.suffix"));
//		for (String string : extList) {
//			System.out.println(string);
//		}
		
//		System.out.println(UploadUtils.getMonthPath());
		String s = "imfile\\201612\\QQ图片20161202085304.jpg";
		System.out.println(s.replaceAll("\\\\", "/"));
	}
}
