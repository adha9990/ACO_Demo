
public class Ant {
	private int tourSize; // 路徑長度
	private boolean[] visited; // 是否走過
	
	public int[] tour; // 已走訪城市
	public double length; // 已走訪路徑長度
	
	// 初始化
	public Ant(int tourSize) {
		this.tourSize = tourSize;
		this.tour = new int[tourSize];
		this.visited = new boolean[tourSize];
	}
	
	// 螞蟻造訪順序，造訪城市
	public void visit(int cityIndex, int city) {
		this.tour[cityIndex] = city;
		this.visited[city] = true;
	}
	
	// 檢查是否走訪過該城市
	public boolean isVisited(int city) {
		return this.visited[city];
	}
	
	// 清除走訪痕跡
	public void clear() {
		for(int i = 0; i < this.tourSize; i++) {
			this.visited[i] = false;
		}
	}
	
}