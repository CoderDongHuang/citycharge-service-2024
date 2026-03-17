package com.citycharge.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MapLoader {
    
    @Value("${citycharge.map.file-path:classpath:map/map.txt}")
    private String mapFilePath;
    
    @Value("${citycharge.map.width:100}")
    private Integer mapWidth;
    
    @Value("${citycharge.map.height:100}")
    private Integer mapHeight;
    
    private final ResourceLoader resourceLoader;
    
    public MapLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public int[][] loadMap() {
        int[][] map = new int[mapHeight][mapWidth];
        
        try {
            Resource resource = resourceLoader.getResource(mapFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < mapHeight) {
                String[] numbers = line.trim().split("\\s+");
                for (int col = 0; col < Math.min(numbers.length, mapWidth); col++) {
                    map[row][col] = Integer.parseInt(numbers[col]);
                }
                row++;
            }
            reader.close();
        } catch (Exception e) {
            // 如果文件不存在，创建默认地图
            createDefaultMap(map);
        }
        
        return map;
    }
    
    private void createDefaultMap(int[][] map) {
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                map[i][j] = 0; // 默认都是道路
            }
        }
        
        // 添加一些默认的换电站和障碍
        map[10][10] = 1; // 换电站
        map[50][50] = 1; // 换电站
        map[90][90] = 1; // 换电站
        
        // 添加一些障碍
        for (int i = 30; i < 40; i++) {
            for (int j = 30; j < 40; j++) {
                map[i][j] = 2; // 障碍
            }
        }
    }
    
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }
    
    public boolean isRoad(int x, int y, int[][] map) {
        return isValidPosition(x, y) && map[y][x] == 0;
    }
    
    public boolean isChargingStation(int x, int y, int[][] map) {
        return isValidPosition(x, y) && map[y][x] == 1;
    }
    
    public boolean isObstacle(int x, int y, int[][] map) {
        return isValidPosition(x, y) && map[y][x] == 2;
    }
}