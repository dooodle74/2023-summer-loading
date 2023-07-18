import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private int weight;

	// 物品在箱中的位置： 坐标xyz
	private int startX;
	private int startY;
	private int startZ;

	// 物品是否经过90度旋转
	private boolean isRotated;

	/**
	 * 创建物品
	 * 
	 * @param number 序号
	 * @param length 长度
	 * @param width  宽度
	 * @param height 高度
	 * @param weight 重量
	 */
	public Item(int number, int length, int width, int height, int weight) {
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

	public int getWeight() {
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
		return Integer.compare(item2.getWeight(), item1.getWeight());
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
	private int weight;
	private int[][][] space;
	private int cumX;
	private int cumY;
	private int cumZ;

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

	public int getWeight() {
		return weight;
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
	public void addWeight(int value) {
		weight += value;
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
		this.cumX += item.getStartX() + item.getLength() / 2.0;
		this.cumY += item.getStartY() + item.getWidth() / 2.0;
		this.cumZ += item.getStartZ() + item.getHeight() / 2.0;
		return true;
	}

	/**
	 * 计算箱子中心
	 * 
	 * @return [X重心，Y重心，Z重心]
	 */
	public double[] getCOM() {
		double comX = this.cumX / (double) this.weight;
		double comY = this.cumY / (double) this.weight;
		double comZ = this.cumZ / (double) this.weight;
		double[] com = { comX, comY, comZ };
		return com;
	}

}

/**
 * Plane 代表飞机
 * 
 * @version 1.6.28
 * @since 1.6.28
 */
class Plane {
	private int length;
	private double weight;
	private int centerMass;
	private double changeCOM;

	public Plane(int length, double weight, int centerMass, double changeCOM) {
		this.length = length;
		this.weight = weight;
		this.centerMass = centerMass;
		this.changeCOM = changeCOM;
	}
}

/**
 * 主要装箱算法
 * 
 * @version 1.7.18
 * @since 1.6.20
 */
class LoadingAlgorithm {

	/**
	 * 主要算法，将所有物品装箱并打出所在位置和旋转情况
	 * 
	 * @param items     所有的物品，以list的形式
	 * @param container 第0箱子，有以后箱子的属性
	 */
	public static void loadItems(List<Item> items, Container container) {
		List<Container> containers = new ArrayList<>();
		containers.add(new Container(container.getLength(), container.getWidth(), container.getHeight()));

		//items.sort(new ItemSortingComparator());
		
		//优化1
		sortItems(items, container.getLength(), container.getWidth(), container.getHeight());
		
		//优化2
		smartRotation(items, container);
		
		for (Item item : items) {
			boolean itemPlaced = false;

			for (int i = 0; i < containers.size(); i++) {
				Container currentContainer = containers.get(i);
				if (placeItemInContainer(item, currentContainer)) {
					System.out.println("#" + item.getNumber() + " in " + (i + 1) + " @(" + item.getStartX() + ", "
							+ item.getStartY() + ", " + item.getStartZ() + ") Rotated: " + item.getRotation());
					itemPlaced = true;
					break;
				}
			}

			if (!itemPlaced) {
				Container newContainer = new Container(container.getLength(), container.getWidth(),
						container.getHeight());
				containers.add(newContainer);
				System.out.println("#" + item.getNumber() + " in new " + containers.size() + " @(" + item.getStartX()
						+ ", " + item.getStartY() + ", " + item.getStartZ() + ") Rotated: " + item.getRotation());
				placeItemInContainer(item, newContainer);
			}
		}
		System.out.println("\nItems Done\n");
		printContainerUsagePercentages(containers);
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
			System.out.println(
					"Container " + (i + 1) + ": Weight: " + container.getWeight() + "; " + usagePercentage + "%");
		}
	}
	
	/**
	 * 优化1：智能排序
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
	    
	    // You can consider other factors like weight, space utilization, etc. to calculate the fit score

	    return utilizationRatio;
	}
	
	/**
	 * 优化2：智能旋转
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
}

/**
 * Main运行程序。所有数据再此输入。
 * 
 * @version 1.6.20
 * @since 1.6.20
 */
public class Main {
	public static void main(String[] args) {
		System.out.println("Test 6:");
		test6();
		System.out.println("Test 6 Complete \n--------------------");
	}

	/**
	 * 测试数据0： 测试格式 将2件10x10x10的物品放进20x20x20的箱子中
	 */
	public static void test0() {
		Container container = new Container(20, 20, 20);

		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			items.add(new Item(i, 10, 10, 10, 5));
		}

		LoadingAlgorithm.loadItems(items, container);
	}

	/**
	 * 测试数据1： 完美空间利用 将270件10x10x10的物品放进30x30x30的箱子中
	 */
	public static void test1() {
		Container container = new Container(30, 30, 30);

		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= 270; i++) {
			items.add(new Item(i, 10, 10, 10, 5));
		}

		LoadingAlgorithm.loadItems(items, container);
	}

	/**
	 * 测试数据2：大中小 将300件10x9x5的物品和200件5x4x3的物品和2000件1x2x2的物品放入45x40x35的箱子中
	 */
	public static void test2() {
		Container container = new Container(45, 40, 35);

		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= 300; i++) {
			items.add(new Item(i, 10, 9, 5, 5));
		}
		for (int i = 301; i <= 500; i++) {
			items.add(new Item(i, 5, 4, 3, 5));
		}
		for (int i = 501; i <= 2500; i++) {
			items.add(new Item(i, 1, 2, 2, 5));
		}

		LoadingAlgorithm.loadItems(items, container);
	}

	/**
	 * 测试数据3：小性随机 将10000件三方尺寸随机5-10不等的物品放入100x100x75的箱子中
	 */
	/*
	 * public static void test3() { Container container = new Container(100, 100,
	 * 75);
	 * 
	 * List<Item> items = new ArrayList<>(); for (int i=1; i<=10000; i++) {
	 * items.add(new Item(i, (int)(Math.random()*5+5), (int)(Math.random()*5+5),
	 * (int)(Math.random()*5+5), 10)); }
	 * 
	 * LoadingAlgorithm.loadItems(items, container); }
	 */

	/**
	 * 测试数据4：中性随机 将1000件三方尺寸随机5-30不等的物品放入100x100x55的箱子中
	 */
	public static void test4() {
		Container container = new Container(100, 100, 55);

		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= 1000; i++) {
			items.add(new Item(i, (int) (Math.random() * 25 + 5), (int) (Math.random() * 25 + 5),
					(int) (Math.random() * 25 + 5), 10));
		}

		LoadingAlgorithm.loadItems(items, container);
	}

	/**
	 * 测试数据5：大型随机 将250件三方尺寸随机40-55不等的物品放入100x100x75的箱子中
	 */
	public static void test5() {
		Container container = new Container(100, 100, 55);

		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= 250; i++) {
			items.add(new Item(i, (int) (Math.random() * 15 + 40), (int) (Math.random() * 15 + 40),
					(int) (Math.random() * 15 + 40), 10));
		}

		LoadingAlgorithm.loadItems(items, container);
	}

	/**
	 * 测试数据6： 实际情况自定义。 所有尺寸为厘米(cm), 重量（kg）
	 */
	public static void test6() {
		// 标准10ft集装箱内部空间(cm)。 https://www.mrbox.co.uk/container-dimensions/
		Container container = new Container(284, 239, 235);

		int i = 1;
		List<Item> items = new ArrayList<>();

		// 10x飞机引擎
		while (i <= 10) {
			items.add(new Item(i, 280, 230, 110, 1000));
			i++;
		}

		// 20x卫星
		while (i <= 30) {
			items.add(new Item(i, 80, 50, 100, 300));
			i++;
		}

		// 20x帐篷
		while (i <= 50) {
			items.add(new Item(i, 140, 235, 230, 50));
			i++;
		}

		// 50x大型收音机
		while (i <= 100) {
			items.add(new Item(i, 48, 25, 10, 15));
			i++;
		}

		// 100x燃油桶
		while (i <= 200) {
			items.add(new Item(i, 30, 30, 45, 10));
			i++;
		}

		// 30x应急资源包
		while (i <= 230) {
			items.add(new Item(i, 75, 65, 40, 500));
			i++;
		}

		// 50x背包
		while (i <= 280) {
			items.add(new Item(i, 70, 45, 30, 20));
			i++;
		}

		// 50x头盔
		while (i <= 330) {
			items.add(new Item(i, 30, 30, 20, 2));
			i++;
		}

		// 150x防弹片
		while (i <= 480) {
			items.add(new Item(i, 28, 22, 2, 3));
			i++;
		}

		// 100x子弹箱:
		// https://www.armyandoutdoors.com/blogs/news/an-introduction-to-ammo-cans
		while (i <= 580) {
			items.add(new Item(i, 45, 25, 15, 6));
			i++;
		}

		// 25x步枪箱:
		while (i <= 605) {
			items.add(new Item(i, 120, 20, 10, 15));
			i++;
		}

		// 150x小袋(食品等）
		while (i <= 755) {
			items.add(new Item(i, 20, 13, 10, 1));
			i++;
		}

		// 245x零碎
		while (i <= 1000) {
			items.add(new Item(i, 12, 8, 5, 1));
			i++;
		}

		LoadingAlgorithm.loadItems(items, container);

	}
} // end