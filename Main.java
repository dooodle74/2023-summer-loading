import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
/**
 * Item 代表单个物品
 * 
 * @version 1.6.20
 * @since 1.6.20
 */
class Item {

	// 物品的属性： 序列号，长宽高，重量
	private int number;
	private int length;
	private int width;
	private int height;
	private double weight;

	// 物品在箱中的位置： 坐标xyz
	private int startX;
	private int startY;
	private int startZ;

	// 物品是否经过90度旋转
	private boolean isRotated;

	// 物品最终放置的箱子
	private int containerNumber;

	/**
	 * 创建物品
	 * 
	 * @param number 序号
	 * @param length 长度
	 * @param width  宽度
	 * @param height 高度
	 * @param weight 重量
	 */
	public Item(int number, int length, int width, int height, double weight) {
		this.number = number;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.startX = 0;
		this.startY = 0;
		this.startZ = 0;
		this.isRotated = false;
	}

	public int getNumber() {
		return number;
	}

	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getWeight() {
		return weight;
	}

	public int getVolume() {
		return length * width * height;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getStartZ() {
		return startZ;
	}

	public void setStartZ(int startZ) {
		this.startZ = startZ;
	}

	public int getContainer() {
		return containerNumber;
	}

	public void setContainer(int containerNum) {
		this.containerNumber = containerNum;
	}

	public void rotate() {
		int temp = length;
		length = width;
		width = temp;
		if (isRotated)
			isRotated = false;
		else
			isRotated = true;
	}

	public boolean getRotation() {
		return isRotated;
	}

	@Override
	public String toString() {
		return "Item{" + "number=" + number + ", length=" + length + ", width=" + width + ", height=" + height
				+ ", weight=" + weight + '}';
	}
}

/**
 * 对序列物品按大小进行排序
 */
class ItemSortingComparator implements Comparator<Item> {
	@Override
	public int compare(Item item1, Item item2) {
		// Sort by volume in descending order
		int volumeComparison = Integer.compare(item2.getVolume(), item1.getVolume());
		if (volumeComparison != 0) {
			return volumeComparison;
		}

		// Sort by length in descending order
		int lengthComparison = Integer.compare(item2.getLength(), item1.getLength());
		if (lengthComparison != 0) {
			return lengthComparison;
		}

		// Sort by width in descending order
		int widthComparison = Integer.compare(item2.getWidth(), item1.getWidth());
		if (widthComparison != 0) {
			return widthComparison;
		}

		// Sort by weight in descending order
		return Double.compare(item2.getWeight(), item1.getWeight());
	}
}

/**
 * Container 代表箱子
 * 
 * @version 1.6.20
 * @since 1.6.20
 */
class Container {
	private int length;
	private int width;
	private int height;
	private double weight;
	private int[][][] space;
	private int cumX;
	private int cumY;
	private int cumZ;
	private int numItems;

	/**
	 * 创建箱子
	 * 
	 * @param length 长度
	 * @param width  宽度
	 * @param height 高度
	 */
	public Container(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.space = new int[length][width][height];
		this.weight = 0;
		this.cumX = 0;
		this.cumY = 0;
		this.cumZ = 0;
		this.numItems = 0;
	}
	
	public Container() {
		this.numItems = 0;
	}
	
	public void postInitialize(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.space = new int[length][width][height];
		this.weight = 0;
		this.cumX = 0;
		this.cumY = 0;
		this.cumZ = 0;
		this.numItems = 0;
	}

	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getWeight() {
		return weight;
	}

	public int getItemCount() {
		return numItems;
	}
	
	
	/**
	 * 在具体的位置是否有空位
	 * 
	 * @param x X坐标
	 * @param y Y坐标
	 * @param z Z坐标
	 * @return 1如果已占或坐标出界； 0如果当前位置有空
	 */
	public int getSpace(int x, int y, int z) {
		if (x < 0 || x >= length)
			return 1;
		if (y < 0 || y >= width)
			return 1;
		if (z < 0 || z >= height)
			return 1;

		return space[x][y][z];
	}

	/**
	 * 设置具体位置占据情况
	 * 
	 * @param x     X坐标
	 * @param y     Y坐标
	 * @param z     Z坐标
	 * @param value 设置值。0为空出，1为占用
	 */
	public void setSpace(int x, int y, int z, int value) {
		space[x][y][z] = value;
	}

	/**
	 * 物品摆设完成时给箱子加上适当重量
	 * 
	 * @param value 加重值
	 */
	public void addWeight(double value) {
		this.weight += value;
	}

	/**
	 * 箱子装在完成时空间利用率
	 * 
	 * @return 一个百分数，利用空间/总空间
	 */
	public double calculateUsagePercentage() {
		int totalSpace = length * width * height;
		int usedSpace = 0;
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < width; y++) {
				for (int z = 0; z < height; z++) {
					if (getSpace(x, y, z) == 1) {
						usedSpace++;
					}
				}
			}
		}
		return (double) usedSpace / totalSpace * 100;
	}

	/**
	 * 装入物品，更新箱子总坐标和总重量
	 * 
	 * @param item 要加的物品
	 * @return true
	 */
	public boolean addItem(Item item) {
		this.addWeight(item.getWeight());
		this.cumX += (item.getStartX() + item.getLength() / 2.0) * item.getWeight();
		this.cumY += (item.getStartY() + item.getWidth() / 2.0) * item.getWeight();
		this.cumZ += (item.getStartZ() + item.getHeight() / 2.0) * item.getWeight();
		this.numItems++;
		return true;
	}

	/**
	 * 计算箱子中心
	 * 
	 * @return [X重心，Y重心，Z重心]
	 */
	public double[] getCOM() {
		double comX = this.cumX / this.weight;
		double comY = this.cumY / this.weight;
		double comZ = this.cumZ / this.weight;
		double[] com = { comX, comY, comZ };
		return com;
	}

}

/**
 * 主要装箱算法
 * 
 * @version 1.7.19
 * @since 1.6.20
 */
class LoadingAlgorithm {

	private static int totalCount;
	private static int currentCount;

	/**
	 * 主要算法，将所有物品装箱并打出所在位置和旋转情况
	 * 
	 * @param items     所有的物品，以list的形式
	 * @param container 第0箱子，有以后箱子的属性
	 */
	public static void loadItems(List<Item> items, Container container) {
		System.out.println("Calculating...");
		
		List<Container> containers = new ArrayList<>();
		containers.add(new Container(container.getLength(), container.getWidth(), container.getHeight()));

		// items.sort(new ItemSortingComparator());

		// 优化1
		sortItems(items, container.getLength(), container.getWidth(), container.getHeight());

		// 优化2
		smartRotation(items, container);

		// percentage calculator
		totalCount = items.size();
		currentCount = 1;

		for (Item item : items) {
			boolean itemPlaced = false;

			for (int i = 0; i < containers.size(); i++) {
				Container currentContainer = containers.get(i);
				if (placeItemInContainer(item, currentContainer)) {
					item.setContainer(i);
					// printItem(item);
					printPercentage();
					currentCount++;
					itemPlaced = true;
					break;
				}
			}

			if (!itemPlaced) {
				Container newContainer = new Container(container.getLength(), container.getWidth(),
						container.getHeight());
				containers.add(newContainer);
				item.setContainer(containers.size());
				// printItem(item);
				printPercentage();
				currentCount++;
				placeItemInContainer(item, newContainer);
			}
		}
		System.out.println("\n\nLoading Instructions: ");

		sortItemsForPackingInstruction(items);

		for (Item item : items) {
			printFinalOrder(item);
		}
		
		System.out.println("\nAll Containers: \n");
		printContainerUsagePercentages(containers);
		System.out.println();
	}

	/**
	 * 将物品放置箱子
	 * 
	 * @param item      物品
	 * @param container 要装的箱子
	 * @return true如果本箱子有空且装箱完毕； false如果本箱子没空，无法装箱
	 */
	private static boolean placeItemInContainer(Item item, Container container) {
		int itemLength = item.getLength();
		int itemWidth = item.getWidth();
		int itemHeight = item.getHeight();

		for (int z = 0; z <= container.getHeight() - itemHeight; z++) {
			for (int y = 0; y <= container.getWidth() - itemWidth; y++) {
				for (int x = 0; x <= container.getLength() - itemLength; x++) {
					if (isSpaceAvailable(x, y, z, item, container)) {
						updateContainerSpace(x, y, z, item, container);
						item.setStartX(x);
						item.setStartY(y);
						item.setStartZ(z);
						return container.addItem(item);
					} else {
						item.rotate();
						if (isSpaceAvailable(x, y, z, item, container)) {
							updateContainerSpace(x, y, z, item, container);
							item.setStartX(x);
							item.setStartY(y);
							item.setStartZ(z);
							return container.addItem(item);
						} else {
							item.rotate(); // rotate back
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * 判断一个物品在一个箱子的某个位置时候有空
	 * 
	 * @param startX    当前X坐标
	 * @param startY    当前Y坐标
	 * @param startZ    当前Z坐标
	 * @param item      当前物品，含有具体参数
	 * @param container 当前箱子
	 * @return true如果在指定位置能装下，false如果在指定位置无法装下
	 */
	private static boolean isSpaceAvailable(int startX, int startY, int startZ, Item item, Container container) {
		int itemLength = item.getLength();
		int itemWidth = item.getWidth();
		int itemHeight = item.getHeight();

		for (int z = startZ; z < startZ + itemHeight; z++) {
			for (int y = startY; y < startY + itemWidth; y++) {
				for (int x = startX; x < startX + itemLength; x++) {
					if (container.getSpace(x, y, z) == 1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 更新箱子空间运用情况，将新加入的物品在当前箱子的所占空间进行更新
	 * 
	 * @param startX    物品X位置
	 * @param startY    物品Y位置
	 * @param startZ    物品Z位置
	 * @param item      当前物品
	 * @param container 当前箱子
	 */
	private static void updateContainerSpace(int startX, int startY, int startZ, Item item, Container container) {
		int itemLength = item.getLength();
		int itemWidth = item.getWidth();
		int itemHeight = item.getHeight();

		for (int z = startZ; z < startZ + itemHeight; z++) {
			for (int y = startY; y < startY + itemWidth; y++) {
				for (int x = startX; x < startX + itemLength; x++) {
					container.setSpace(x, y, z, 1);
				}
			}
		}
	}

	/**
	 * 打出所有箱子最后的利用率
	 * 
	 * @param containers 所有的箱子，list的形式
	 * @see Container.calculateUsagePercentage
	 */
	private static void printContainerUsagePercentages(List<Container> containers) {
		for (int i = 0; i < containers.size(); i++) {
			Container container = containers.get(i);
			double usagePercentage = container.calculateUsagePercentage();
			double COM[] = container.getCOM();
			int comX = (int) COM[0];
			int comY = (int) COM[1];
			int comZ = (int) COM[2];
			
			System.out.println(
					"[" + (i + 1) + "] Items: " + container.getItemCount() + "; Weight: " + container.getWeight() + "; Centre: (" + comX + ", " + comY + ", " + comZ + ")" );
		}
	}

	/**
	 * 优化1：智能排序
	 * 
	 * @param items
	 * @param containerLength
	 * @param containerWidth
	 * @param containerHeight
	 */
	public static void sortItems(List<Item> items, int containerLength, int containerWidth, int containerHeight) {
		Collections.sort(items, (item1, item2) -> {
			// Calculate fit score for each item
			double fitScore1 = calculateFitScore(item1, containerLength, containerWidth, containerHeight);
			double fitScore2 = calculateFitScore(item2, containerLength, containerWidth, containerHeight);

			// Sort based on fit score in descending order
			return Double.compare(fitScore2, fitScore1);
		});
	}

	private static double calculateFitScore(Item item, int containerLength, int containerWidth, int containerHeight) {
		// Calculate fit score based on item dimensions and container size
		double itemVolume = item.getLength() * item.getWidth() * item.getHeight();
		double containerVolume = containerLength * containerWidth * containerHeight;
		double utilizationRatio = itemVolume / containerVolume;

		// You can consider other factors like weight, space utilization, etc. to
		// calculate the fit score

		return utilizationRatio;
	}

	/**
	 * 优化2：智能旋转
	 * 
	 * @param items
	 * @param container
	 */
	public static void smartRotation(List<Item> items, Container container) {
		for (Item item : items) {
			boolean shouldRotate = shouldRotateItem(item, container);
			if (shouldRotate) {
				item.rotate();
			}
		}
	}

	private static boolean shouldRotateItem(Item item, Container container) {
		int itemLength = item.getLength();
		int itemWidth = item.getWidth();
		int itemHeight = item.getHeight();

		int availableLength = container.getLength();
		int availableWidth = container.getWidth();
		int availableHeight = container.getHeight();

		// Check if the item can fit without rotation
		if (itemLength <= availableLength && itemWidth <= availableWidth && itemHeight <= availableHeight) {
			return false;
		}

		// Check if the item can fit after rotation
		if (itemWidth <= availableLength && itemLength <= availableWidth && itemHeight <= availableHeight) {
			return true;
		}

		// Check if the item can fit by rotating on another axis
		if (itemHeight <= availableLength && itemWidth <= availableWidth && itemLength <= availableHeight) {
			return true;
		}

		return false;
	}

	public static void printItem(Item item) {
		int percent = 100 * currentCount / totalCount;

		System.out.println("#" + item.getNumber() + " @ [" + (item.getContainer() + 1) + "] (" + item.getStartX() + ", "
				+ item.getStartY() + ", " + item.getStartZ() + ") "
				+ (item.getRotation() ? "(YES rotate)" : "(NO rotate)") + "..." + percent + "%...");
	}

	public static void printPercentage() {
		int percent = 100 * currentCount / (totalCount + 1);
		System.out.print("\r" + percent + "%...");
	}

	public static void printFinalOrder(Item item) {
		System.out.println("#" + item.getNumber() + " @ [" + (item.getContainer() + 1) + "] (" + item.getStartX() + ", "
				+ item.getStartY() + ", " + item.getStartZ() + ") "
				+ (item.getRotation() ? "(YES rotate)" : "(NO rotate)"));
	}

	public static void sortItemsForPackingInstruction(List<Item> items) {
		// Sort items based on their final position in the container (width-side first)
		Collections.sort(items, (item1, item2) -> {
			int compareContainer = Integer.compare(item1.getContainer(), item2.getContainer());
			int compareX = Integer.compare(item1.getStartX(), item2.getStartX());
			int compareY = Integer.compare(item1.getStartY(), item2.getStartY());
			int compareZ = Integer.compare(item1.getStartZ(), item2.getStartZ());

			// 0: sort by container
			if (compareContainer != 0) {
				return compareContainer;
			}

			// First, sort items based on their x-coordinate (width-side)
			if (compareX != 0) {
				return compareX;
			}

			// If x-coordinates are the same, sort based on their y-coordinate
			if (compareY != 0) {
				return compareY;
			}

			// If x and y-coordinates are the same, sort based on their z-coordinate
			return compareZ;
		});
	}

}

/**
 * Main运行程序。所有数据再此输入。
 * 
 * @version 1.7.19
 * @since 1.6.20
 */
public class Main {
	public static void main(String[] args) {		
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Filepath (C:/***/***.xlsx): ");
		String filepath = s.nextLine();
		try {
            // Load Excel file
            InputStream inputStream = new FileInputStream(filepath);
            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            int rowNum = 1;
            int cellNum = 0;
            int cellA = 0;
            int cellB = 0;
            int cellC = 0;
            double cellD = 0;
            Container container = new Container();
            List<Item> items = new ArrayList<>();
            
            for (Row row : sheet) {
            	cellNum = 0;
            	for (Cell cell : row) {
            		switch(cellNum) {
            		case 0:
            			cellA = (int) cell.getNumericCellValue();
            			cellNum++;
            			break;
            		case 1:
            			cellB = (int) cell.getNumericCellValue();
            			cellNum++;
            			break;
            		case 2:
            			cellC = (int) cell.getNumericCellValue();
            			cellNum++;
            			break;
            		case 3:
            			cellD = cell.getNumericCellValue();
            			cellNum++;
            			break;
            		default:
            			
            		}
            	}
            	
            	if (rowNum == 1) {
            		container.postInitialize(cellA, cellB, cellC);
            	} else {
            		items.add(new Item(rowNum, cellA, cellB, cellC, cellD));
            	}
            	
            	rowNum++;
            	
            }
            
            // Close the workbook and input stream
            workbook.close();
            inputStream.close();
            System.out.println("\nData input SUCCESS");
            LoadingAlgorithm.loadItems(items, container);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

} // end