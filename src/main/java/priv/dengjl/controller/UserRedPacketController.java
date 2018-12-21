package priv.dengjl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import priv.dengjl.service.UserRedPacketByRedisService;
import priv.dengjl.service.UserRedPacketService;

@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController {
	
	@Autowired
	private UserRedPacketService service;
	
	@Autowired
	private UserRedPacketByRedisService redisService;
	
	@RequestMapping(value = "/index")
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("userRedPacket");
		return mv;
	}
	
	@RequestMapping(value = "/getRedPacket/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = service.insertRedPacket(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
	
	@RequestMapping(value = "/getRedPacket2/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket2(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = service.insertRedPacket2(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
	
	@RequestMapping(value = "/getRedPacket3/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket3(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = service.insertRedPacket3(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
	
	// 乐观锁
	@RequestMapping(value = "/getRedPacket4/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket4(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = service.insertRedPacket4(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
	
	// 乐观锁 + 重入机制
	@RequestMapping(value = "/getRedPacket5/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket5(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = service.insertRedPacket5(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
	
	@RequestMapping(value = "/getRedPacket6/{redPacketId}/{userId}", method = RequestMethod.GET)
	public ModelAndView getRedPacket6(@PathVariable("redPacketId") String redPacketId, @PathVariable("userId") String userId) {
		int result = redisService.insertRedPacketByRedis(Integer.parseInt(redPacketId), Integer.parseInt(userId));
		ModelAndView mv = new ModelAndView();
		mv.addObject(result);
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
}
