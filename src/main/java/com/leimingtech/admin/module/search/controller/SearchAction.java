package com.leimingtech.admin.module.search.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.service.module.search.service.GoodsSearchService;
import com.leimingtech.service.module.search.service.GoodsWordsService;
import com.leimingtech.service.module.search.service.StoreSearchService;
import com.leimingtech.service.module.search.service.StoreWordsService;
/**
 * 
 * 创建人：cgl   
 * 创建时间：2015年08月03日16:04:35
 */
@Controller
@RequestMapping("/search")
public class SearchAction {

	@Autowired
	GoodsSearchService goodsSearchService;
	
	@Autowired
	StoreSearchService storeSearchService;
	
	@Autowired
	GoodsWordsService goodsWordsService;
	
	@Autowired
	StoreWordsService storeWordsService;
	
	@RequestMapping("/creteIndexView")
	public String searchTest(){
		return "/search/search-index";
	}
	
	/**
	 * 创建商品索引的action
	 * @return state=1成功为0则失败
	 */
	@ResponseBody
	@RequestMapping("/createGoodsIndex")
	public Map<String, String> saveGoodsIndex(){
		Map<String, String> map = new HashMap<String, String>();
		try{
			Goods goods = new Goods();
			goodsSearchService.saveGoodsIndex(goods);
		map.put("state", "1");
		}catch(Exception e){
			map.put("state", "0");
		}
		return map;
	}
	
	/**
	 * 创建商品索引的action
	 * @return state=1成功为0则失败
	 */
	@ResponseBody
	@RequestMapping("/createStoreIndex")
	public Map<String, String> createStoreIndex(){
		Map<String, String> map = new HashMap<String, String>();
		try{
			storeSearchService.createStoreIndex(null);
			map.put("state", "1");
		}catch(Exception e){
			map.put("state", "0");
		}
		return map;
	}
	
	/**
	 * 初始化商品关键词
	 */
	@ResponseBody
	@RequestMapping("/createGoodsWords")
	public Map<String, String> createGoodsWords(){
		Map<String, String> map = new HashMap<String, String>();
		try{
			goodsWordsService.deleteAndInitGoodsWord();
			map.put("state", "1");
		}catch(Exception e){
			e.printStackTrace();
			map.put("state", "0");
		}
		return map;
	}
	
	/**
	 * 初始化店铺关键词
	 */
	@ResponseBody
	@RequestMapping("/createStoreWords")
	public Map<String, String> createStoreWords(){
		Map<String, String> map = new HashMap<String, String>();
		try{
			storeWordsService.deleteAndInitStoreWord();
			map.put("state", "1");
		}catch(Exception e){
			e.printStackTrace();
			map.put("state", "0");
		}
		return map;
	}
}
