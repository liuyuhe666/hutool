package cn.hutool.core.io;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.file.LineSeparator;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FileUtil} 单元测试类
 *
 * @author Looly
 */
public class FileUtilTest {

	@Test
	public void fileTest1() {
		final File file = FileUtil.file("d:/aaa", "bbb");
		assertNotNull(file);
	}

	@Test
	public void fileTest2() {
		assertThrows(IllegalArgumentException.class, () -> {
			final File file = FileUtil.file("d:/aaa", "bbb");
			assertNotNull(file);

			// 构建目录中出现非子目录抛出异常
			FileUtil.file(file, "../ccc");

			FileUtil.file("E:/");
		});
	}

	@Test
	public void getAbsolutePathTest() {
		final String absolutePath = FileUtil.getAbsolutePath("LICENSE-junit.txt");
		assertNotNull(absolutePath);
		final String absolutePath2 = FileUtil.getAbsolutePath(absolutePath);
		assertNotNull(absolutePath2);
		assertEquals(absolutePath, absolutePath2);

		String path = FileUtil.getAbsolutePath("中文.xml");
		assertTrue(path.contains("中文.xml"));

		path = FileUtil.getAbsolutePath("d:");
		assertEquals("d:", path);

	}

	@Test
	public void smbPathTest() {
		final String smbPath = "\\\\192.168.1.1\\share\\rc-source";
		final String parseSmbPath = FileUtil.getAbsolutePath(smbPath);
		assertEquals(smbPath, parseSmbPath);
		assertTrue(FileUtil.isAbsolutePath(smbPath));
		assertTrue(Paths.get(smbPath).isAbsolute());
	}

	@Test
	@Disabled
	public void touchTest() {
		FileUtil.touch("d:\\tea\\a.jpg");
	}

	@Test
	@Disabled
	public void delTest() {
		// 删除一个不存在的文件，应返回true
		final boolean result = FileUtil.del("e:/Hutool_test_3434543533409843.txt");
		assertTrue(result);
	}

	@Test
	@Disabled
	public void delTest2() {
		// 删除一个不存在的文件，应返回true
		final boolean result = FileUtil.del(Paths.get("e:/Hutool_test_3434543533409843.txt"));
		assertTrue(result);
	}

	@Test
	@Disabled
	public void renameTest() {
		FileUtil.rename(FileUtil.file("d:/test/3.jpg"), "2.jpg", false);
	}

	@Test
	@Disabled
	public void renameTest2() {
		FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/b"), false);
	}

	@Test
	@Disabled
	public void renameTest3() {
		FileUtil.rename(FileUtil.file("d:/test/test2.xlsx"), "test3.xlsx", true);
	}

	@Test
	public void copyTest() {
		final File srcFile = FileUtil.file("hutool.jpg");
		final File destFile = FileUtil.file("hutool.copy.jpg");

		FileUtil.copy(srcFile, destFile, true);

		assertTrue(destFile.exists());
		assertEquals(srcFile.length(), destFile.length());
	}

	@Test
	@Disabled
	public void copyFilesFromDirTest() {
		final File srcFile = FileUtil.file("D:\\驱动");
		final File destFile = FileUtil.file("d:\\驱动备份");

		FileUtil.copyFilesFromDir(srcFile, destFile, true);
	}

	@Test
	@Disabled
	public void copyDirTest() {
		final File srcFile = FileUtil.file("D:\\test");
		final File destFile = FileUtil.file("E:\\");

		FileUtil.copy(srcFile, destFile, true);
	}

	@Test
	@Disabled
	public void moveDirTest() {
		final File srcFile = FileUtil.file("E:\\test2");
		final File destFile = FileUtil.file("D:\\");

		FileUtil.move(srcFile, destFile, true);
	}

	@Test
	@Disabled
	public void renameToSubTest() {
		assertThrows(IllegalArgumentException.class, ()->{
			// 移动到子目录，报错
			FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/a/c"), false);
		});
	}

	@Test
	@Disabled
	public void renameSameTest() {
		// 目标和源相同，不处理
		FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/a"), false);
	}

	@Test
	public void equalsTest() {
		// 源文件和目标文件都不存在
		final File srcFile = FileUtil.file("d:/hutool.jpg");
		final File destFile = FileUtil.file("d:/hutool.jpg");

		final boolean equals = FileUtil.equals(srcFile, destFile);
		assertTrue(equals);

		// 源文件存在，目标文件不存在
		final File srcFile1 = FileUtil.file("hutool.jpg");
		final File destFile1 = FileUtil.file("d:/hutool.jpg");

		final boolean notEquals = FileUtil.equals(srcFile1, destFile1);
		assertFalse(notEquals);
	}

	@Test
	@Disabled
	public void convertLineSeparatorTest() {
		FileUtil.convertLineSeparator(FileUtil.file("d:/aaa.txt"), CharsetUtil.CHARSET_UTF_8, LineSeparator.WINDOWS);
	}

	@Test
	public void normalizeTest() {
		assertEquals("/foo/", FileUtil.normalize("/foo//"));
		assertEquals("/foo/", FileUtil.normalize("/foo/./"));
		assertEquals("/bar", FileUtil.normalize("/foo/../bar"));
		assertEquals("/bar/", FileUtil.normalize("/foo/../bar/"));
		assertEquals("/baz", FileUtil.normalize("/foo/../bar/../baz"));
		assertEquals("/", FileUtil.normalize("/../"));
		assertEquals("foo", FileUtil.normalize("foo/bar/.."));
		assertEquals("../bar", FileUtil.normalize("foo/../../bar"));
		assertEquals("bar", FileUtil.normalize("foo/../bar"));
		assertEquals("/server/bar", FileUtil.normalize("//server/foo/../bar"));
		assertEquals("/bar", FileUtil.normalize("//server/../bar"));
		assertEquals("C:/bar", FileUtil.normalize("C:\\foo\\..\\bar"));
		//
		assertEquals("C:/bar", FileUtil.normalize("C:\\..\\bar"));
		assertEquals("../../bar", FileUtil.normalize("../../bar"));
		assertEquals("C:/bar", FileUtil.normalize("/C:/bar"));
		assertEquals("C:", FileUtil.normalize("C:"));

		// issue#3253，smb保留格式
		assertEquals("\\\\192.168.1.1\\Share\\", FileUtil.normalize("\\\\192.168.1.1\\Share\\"));
	}

	@Test
	public void normalizeBlankTest() {
		assertEquals("C:/aaa ", FileUtil.normalize("C:\\aaa "));
	}

	@Test
	public void normalizeHomePathTest() {
		final String home = FileUtil.getUserHomePath().replace('\\', '/');
		assertEquals(home + "/bar/", FileUtil.normalize("~/foo/../bar/"));
	}

	@Test
	public void normalizeHomePathTest2() {
		final String home = FileUtil.getUserHomePath().replace('\\', '/');
		// 多个~应该只替换开头的
		assertEquals(home + "/~bar/", FileUtil.normalize("~/foo/../~bar/"));
	}

	@Test
	public void normalizeClassPathTest() {
		assertEquals("", FileUtil.normalize("classpath:"));
	}

	@Test
	public void normalizeClassPathTest2() {
		assertEquals("../a/b.csv", FileUtil.normalize("../a/b.csv"));
		assertEquals("../../../a/b.csv", FileUtil.normalize("../../../a/b.csv"));
	}

	@Test
	public void doubleNormalizeTest() {
		final String normalize = FileUtil.normalize("/aa/b:/c");
		final String normalize2 = FileUtil.normalize(normalize);
		assertEquals("/aa/b:/c", normalize);
		assertEquals(normalize, normalize2);
	}

	@Test
	public void subPathTest() {
		final Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");

		Path subPath = FileUtil.subPath(path, 5, 4);
		assertEquals("eee", subPath.toString());
		subPath = FileUtil.subPath(path, 0, 1);
		assertEquals("aaa", subPath.toString());
		subPath = FileUtil.subPath(path, 1, 0);
		assertEquals("aaa", subPath.toString());

		// 负数
		subPath = FileUtil.subPath(path, -1, 0);
		assertEquals("aaa/bbb/ccc/ddd/eee", subPath.toString().replace('\\', '/'));
		subPath = FileUtil.subPath(path, -1, Integer.MAX_VALUE);
		assertEquals("fff", subPath.toString());
		subPath = FileUtil.subPath(path, -1, path.getNameCount());
		assertEquals("fff", subPath.toString());
		subPath = FileUtil.subPath(path, -2, -3);
		assertEquals("ddd", subPath.toString());
	}

	@Test
	public void subPathTest2() {
		String subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc/");
		assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/");
		assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/test.txt");
		assertEquals("ccc/test.txt", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc");
		assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc");
		assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb");
		assertEquals("", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb");
		assertEquals("", subPath);
	}

	@Test
	public void getPathEle() {
		final Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");

		Path ele = FileUtil.getPathEle(path, -1);
		assertEquals("fff", ele.toString());
		ele = FileUtil.getPathEle(path, 0);
		assertEquals("aaa", ele.toString());
		ele = FileUtil.getPathEle(path, -5);
		assertEquals("bbb", ele.toString());
		ele = FileUtil.getPathEle(path, -6);
		assertEquals("aaa", ele.toString());
	}

	@Test
	public void listFileNamesTest() {
		List<String> names = FileUtil.listFileNames("classpath:");
		assertTrue(names.contains("hutool.jpg"));

		names = FileUtil.listFileNames("");
		assertTrue(names.contains("hutool.jpg"));

		names = FileUtil.listFileNames(".");
		assertTrue(names.contains("hutool.jpg"));
	}

	@Test
	@Disabled
	public void listFileNamesInJarTest() {
		final List<String> names = FileUtil.listFileNames("d:/test/hutool-core-5.1.0.jar!/cn/hutool/core/util ");
		for (final String name : names) {
			Console.log(name);
		}
	}

	@Test
	@Disabled
	public void listFileNamesTest2() {
		final List<String> names = FileUtil.listFileNames("D:\\m2_repo\\commons-cli\\commons-cli\\1.0\\commons-cli-1.0.jar!org/apache/commons/cli/");
		for (final String string : names) {
			Console.log(string);
		}
	}

	@Test
	@Disabled
	public void loopFilesTest() {
		final List<File> files = FileUtil.loopFiles("d:/");
		for (final File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	@Disabled
	public void loopFilesTest2() {
		FileUtil.loopFiles("").forEach(Console::log);
	}

	@Test
	@Disabled
	public void loopFilesWithDepthTest() {
		final List<File> files = FileUtil.loopFiles(FileUtil.file("d:/m2_repo"), 2, null);
		for (final File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	public void getParentTest() {
		// 只在Windows下测试
		if (FileUtil.isWindows()) {
			File parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 0);
			assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc\\ddd"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 1);
			assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 2);
			assertEquals(FileUtil.file("d:\\aaa\\bbb"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 4);
			assertEquals(FileUtil.file("d:\\"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 5);
			assertNull(parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 10);
			assertNull(parent);
		}
	}

	@Test
	public void lastIndexOfSeparatorTest() {
		final String dir = "d:\\aaa\\bbb\\cc\\ddd";
		final int index = FileUtil.lastIndexOfSeparator(dir);
		assertEquals(13, index);

		final String file = "ddd.jpg";
		final int index2 = FileUtil.lastIndexOfSeparator(file);
		assertEquals(-1, index2);
	}

	@Test
	public void getNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String name = FileUtil.getName(path);
		assertEquals("ddd", name);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		name = FileUtil.getName(path);
		assertEquals("ddd.jpg", name);
	}

	@Test
	public void mainNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String mainName = FileUtil.mainName(path);
		assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd";
		mainName = FileUtil.mainName(path);
		assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		mainName = FileUtil.mainName(path);
		assertEquals("ddd", mainName);
	}

	@Test
	public void extNameTest() {
		String path =  FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd\\" : "~/Desktop/hutool/ddd/";
		String mainName = FileUtil.extName(path);
		assertEquals("", mainName);

		path =  FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd" : "~/Desktop/hutool/ddd";
		mainName = FileUtil.extName(path);
		assertEquals("", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd.jpg" : "~/Desktop/hutool/ddd.jpg";
		mainName = FileUtil.extName(path);
		assertEquals("jpg", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.xlsx" : "~/Desktop/hutool/fff.xlsx";
		mainName = FileUtil.extName(path);
		assertEquals("xlsx", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.gz" : "~/Desktop/hutool/fff.tar.gz";
		mainName = FileUtil.extName(path);
		assertEquals("tar.gz", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.Z" : "~/Desktop/hutool/fff.tar.Z";
		mainName = FileUtil.extName(path);
		assertEquals("tar.Z", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.bz2" : "~/Desktop/hutool/fff.tar.bz2";
		mainName = FileUtil.extName(path);
		assertEquals("tar.bz2", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.xz" : "~/Desktop/hutool/fff.tar.xz";
		mainName = FileUtil.extName(path);
		assertEquals("tar.xz", mainName);
	}

	@Test
	public void getWebRootTest() {
		final File webRoot = FileUtil.getWebRoot();
		assertNotNull(webRoot);
		assertEquals("hutool-core", webRoot.getName());
	}

	@Test
	public void getMimeTypeTest() {
		String mimeType = FileUtil.getMimeType("test2Write.jpg");
		assertEquals("image/jpeg", mimeType);

		mimeType = FileUtil.getMimeType("test2Write.html");
		assertEquals("text/html", mimeType);

		mimeType = FileUtil.getMimeType("main.css");
		assertEquals("text/css", mimeType);

		mimeType = FileUtil.getMimeType("test.js");
		// 在 jdk 11+ 会获取到 text/javascript,而非 自定义的 application/x-javascript
		final List<String> list = ListUtil.of("text/javascript", "application/x-javascript");
		assertTrue(list.contains(mimeType));

		// office03
		mimeType = FileUtil.getMimeType("test.doc");
		assertEquals("application/msword", mimeType);
		mimeType = FileUtil.getMimeType("test.xls");
		assertEquals("application/vnd.ms-excel", mimeType);
		mimeType = FileUtil.getMimeType("test.ppt");
		assertEquals("application/vnd.ms-powerpoint", mimeType);

		// office07+
		mimeType = FileUtil.getMimeType("test.docx");
		assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", mimeType);
		mimeType = FileUtil.getMimeType("test.xlsx");
		assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", mimeType);
		mimeType = FileUtil.getMimeType("test.pptx");
		assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation", mimeType);

		// pr#2617@Github
		mimeType = FileUtil.getMimeType("test.wgt");
		assertEquals("application/widget", mimeType);

		// issue#3092
		mimeType = FileUtil.getMimeType("https://xxx.oss-cn-hangzhou.aliyuncs.com/xxx.webp");
		assertEquals("image/webp", mimeType);
	}

	@Test
	public void isSubTest() {
		final File file = new File("d:/test");
		final File file2 = new File("d:/test2/aaa");
		assertFalse(FileUtil.isSub(file, file2));
	}

	@Test
	public void isSubRelativeTest() {
		final File file = new File("..");
		final File file2 = new File(".");
		assertTrue(FileUtil.isSub(file, file2));
	}

	@Test
	@Disabled
	public void appendLinesTest(){
		final List<String> list = ListUtil.toList("a", "b", "c");
		FileUtil.appendLines(list, FileUtil.file("d:/test/appendLines.txt"), CharsetUtil.CHARSET_UTF_8);
	}

	@Test
	@Disabled
	public void createTempFileTest(){
		final File nullDirTempFile = FileUtil.createTempFile();
		assertTrue(nullDirTempFile.exists());

		final File suffixDirTempFile = FileUtil.createTempFile(".xlsx",true);
		assertEquals("xlsx", FileUtil.getSuffix(suffixDirTempFile));

		final File prefixDirTempFile = FileUtil.createTempFile("prefix",".xlsx",true);
		assertTrue(FileUtil.getPrefix(prefixDirTempFile).startsWith("prefix"));
	}

	@Test
	public void getTotalLinesTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines.csv"));
		assertEquals(8, totalLines);
	}

	@Test
	public void getTotalLinesCrTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines_cr.csv"));
		assertEquals(8, totalLines);
	}

	@Test
	public void getTotalLinesCrlfTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines_crlf.csv"));
		assertEquals(8, totalLines);
	}

	@Test
	public void issue3591Test() {
		// 此文件最后一行末尾无换行符
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("1_psi_index_0.txt"));
		assertEquals(11, totalLines);
	}

	@Test
	public void isAbsolutePathTest(){
		String path = "d:/test\\aaa.txt";
		assertTrue(FileUtil.isAbsolutePath(path));

		path = "test\\aaa.txt";
		assertFalse(FileUtil.isAbsolutePath(path));
	}

	@Test
	@Disabled
	public void copyTest2(){
		final File copy = FileUtil.copy("d:/test/qrcodeCustom.png", "d:/test/pic", false);
		// 当复制文件到目标目录的时候，返回复制的目标文件，而非目录
		Console.log(copy);
	}

	@Test
	public void checkSlipTest() {
		assertThrows(IllegalArgumentException.class, ()->{
			FileUtil.checkSlip(FileUtil.file("test/a"), FileUtil.file("test/../a"));
		});
	}
}
