import java.util.Arrays;

public class ACO {
	// 可更改
	
	// 由試誤法嘗試，通常 beta > alpha 以獲得最佳結果
    private double alpha = 1; // 賀爾蒙重要性
    private double beta = 5; // 距離重要性
	private double c = 1.0; // 模擬開始時的賀爾蒙濃度
	private double evaporation = 0.8; // 賀爾蒙衰退參數，可設為0.8 ~ 0.9之間
	private double Q = 100; // 賀爾蒙強度
	private double antFactor = 1; // 螞蟻的數量，建議 螞蟻數量 = 節點總數量
	
	private int maxIterator = 100; // 執行次數
	
	// 不可更改
	
	// 儲存用
	private int numberOfCity; // 節點數量
	private int numberOfAnt; // 螞蟻數量
	
	private double[][] visibility; // 能見度
	private double[][] pheromone; // 賀爾蒙
	
	private City[] cities; // 城市
	private Ant[] ants; // 螞蟻
	
	// 計算用
	private int currentIndex; // 紀錄螞蟻當前造訪城市索引
	private double[] probability; // 計算造訪城市機率
	
	// 最佳路徑
	private int[] bestTourOrder = null; // 最佳旅行順序
	private double bestTourLength = 0; // 最佳旅行順序
	
	// 初始化
	public ACO(City[] cities){
		int citySize = cities.length;
		this.numberOfCity = citySize;
		this.numberOfAnt = (int) (this.antFactor * citySize);
		this.cities = cities;
		this.ants = new Ant[this.numberOfAnt];
		this.probability = new double[citySize];
		this.visibility = new double[citySize][citySize];
		this.pheromone = new double[citySize][citySize];
		
		// 將問題轉為路徑形式
		for(int i = 0; i < this.numberOfCity; i++) {
			for(int j = 0; j < this.numberOfCity; j++) {
				this.visibility[i][j] = this.getDistance(cities[i], cities[j]);
			}
		}
		
		// 初始化費洛蒙濃度
		for(int i = 0; i < this.numberOfCity; i++) {
    		for(int j = 0; j < this.numberOfCity; j++) {
    			this.pheromone[i][j] = c;
        	}
    	}
		
		// 初始化螞蟻
		for(int i = 0; i < this.numberOfAnt; i++) {
			this.ants[i] = new Ant(this.numberOfCity);
		}
	}
	
	// 計算兩個城市間的距離
	private double getDistance(City fromCity, City toCity) {
    	double a = Math.pow(fromCity.x - toCity.x, 2);
    	double b = Math.pow(fromCity.y - toCity.y, 2);
		return Math.sqrt(a + b);
	}
	
	// 運行模擬
	public void execute() {
		// 設定停止條件
		for(int i = 0; i < this.maxIterator; i++) {
			// 螞蟻搜尋路徑
			this.setupAnts();
			this.moveAnts();
			// 更新費洛蒙
			this.updatePheromones();
			// 更新最佳路徑
			this.updateBest();
		}
	}
	
	// 將螞蟻隨機投放在各個城市上
	private void setupAnts() {
		this.currentIndex = 0;
		for(int i = 0; i < this.numberOfAnt; i++) {
			Ant ant = this.ants[i];
			ant.clear();
			ant.visit(this.currentIndex, (int) (Math.random() * this.numberOfCity));
		}
	}
	
	// 造訪所有城市並計算總距離
	private void moveAnts() {
		// 造訪所有城市
		for(int nextIndex = 1; nextIndex < this.numberOfCity; nextIndex++) {
			for(int i = 0; i < this.numberOfAnt; i++) {
				Ant ant = this.ants[i];
				int nextCity = this.selectNextCity(ant);
				ant.visit(nextIndex, nextCity);
			}
			this.currentIndex = nextIndex;
		}
		// 計算距離
		for(int i = 0; i < this.numberOfAnt; i++) {
			Ant ant = this.ants[i];
			double total = 0;
			for(int j = 0; j < this.numberOfCity; j++) {
				int fromCity = ant.tour[j];
				int toCity = ant.tour[(j + 1) % this.numberOfCity];
				total += this.visibility[fromCity][toCity];
			}
			ant.length = total;
		}
	}
	
	// 螞蟻選擇下一個造訪的城市
	private int selectNextCity(Ant ant) {
		updateProbarbility(ant);
		return rouletteWheelSelection(ant);
	}
	
	// 更新轉換機率。 i => 當前城市 , j => 欲造訪城市
	private void updateProbarbility(Ant ant) {
		int i = ant.tour[this.currentIndex];
		// 為造訪過城市 * 線段長度倒數
		double denominator = 0; // 分母
		for(int j = 0; j < this.numberOfCity; j++) {
			if(!ant.isVisited(j)) {
				denominator += Math.pow(this.pheromone[i][j], this.alpha) * Math.pow(1 / this.visibility[i][j], this.beta);
			}
		}
		for(int j = 0; j < this.numberOfCity; j++) {
			// 欲造訪城市 * 線段長度倒數
			double numerator = 0; // 分子
			
			// i 造訪 j 之機率
			if(ant.isVisited(j)) {
				this.probability[j] = 0;
			}else {
				numerator = Math.pow(this.pheromone[i][j], this.alpha) * Math.pow(1 / this.visibility[i][j], this.beta);
				this.probability[j] = numerator / denominator;				
			}
		}
	}
	
	// 輪盤法隨機造訪城鎮
	private int rouletteWheelSelection(Ant ant) {
		double rnd = Math.random();
		double total = 0;
		for(int i = 0; i < this.numberOfCity; i++) {
			total += this.probability[i];
			if(total > rnd) return i;
		}
		throw new RuntimeException("沒有能造訪的城市");
	}
	
	// 更新費洛蒙濃度，整體更新法
	private void updatePheromones() {
		// 舊賀爾蒙衰退
		for(int i = 0; i < this.numberOfCity; i++) {
			for(int j = 0; j < this.numberOfCity; j++) {
				this.pheromone[i][j] *= 1 - this.evaporation;
			}
		}
		// 新費洛蒙疊加
		for(int i = 0; i < this.numberOfAnt; i++) {
			Ant ant = this.ants[i];
			double pheromoneDropsAmount = this.Q / ant.length;
			for(int j = 0; j < this.numberOfCity; j++) {
				int fromCity = ant.tour[j];
				int toCity = ant.tour[(j + 1) % this.numberOfCity];
				this.pheromone[fromCity][toCity] += pheromoneDropsAmount;
			}
		}
	}
	
	// 更新最佳路徑
	private void updateBest() {
		for(int i = 0; i < this.numberOfAnt; i++) {
			Ant ant = this.ants[i];
			if(this.bestTourOrder == null || ant.length < bestTourLength) {
				this.bestTourOrder = ant.tour;
				this.bestTourLength = ant.length;
				this.test();
			}
		}
		
	}
	
	// 輸出資訊
	private void test() {
        String[] bestTourName = new String[this.numberOfCity];
        for(int i = 0; i < this.numberOfCity; i++) {
        	int index = bestTourOrder[i];
        	bestTourName[i] = cities[index].name;
        }
		System.out.println("-----------------------------------------------------------");
		System.out.println("Best Tour Length : " + bestTourLength);
		System.out.println("Best Tour Order : " + Arrays.toString(bestTourOrder));
		System.out.println("Best Tour : " + Arrays.toString(bestTourName));
	}
}