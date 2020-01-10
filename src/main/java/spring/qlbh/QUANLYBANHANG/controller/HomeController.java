package spring.qlbh.QUANLYBANHANG.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import spring.qlbh.QUANLYBANHANG.dao.DonHangDAO;
import spring.qlbh.QUANLYBANHANG.dao.DongDonHangDAO;
import spring.qlbh.QUANLYBANHANG.dao.HangDAO;
import spring.qlbh.QUANLYBANHANG.dao.LoaiHangDAO;
import spring.qlbh.QUANLYBANHANG.dao.UserDAO;
import spring.qlbh.QUANLYBANHANG.model.DonHangInfo;
import spring.qlbh.QUANLYBANHANG.model.DongDonHangInfo;
import spring.qlbh.QUANLYBANHANG.model.GioHangInfo;
import spring.qlbh.QUANLYBANHANG.model.HangInfo;
import spring.qlbh.QUANLYBANHANG.model.LoaiHangInfo;
import spring.qlbh.QUANLYBANHANG.model.UserInfo;





@Controller
public class HomeController {
	@Autowired
	private HangDAO hangDAO;
	@Autowired
	private LoaiHangDAO loaiHangDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private DonHangDAO donHangDAO;
	@Autowired
	private DongDonHangDAO dongDonHangDAO;
	private static Calendar cal;
	@RequestMapping("/")
	public String indexPage(Model model) {
		List<HangInfo> hang = hangDAO.loadHang();
		
		List<LoaiHangInfo> loaiHang =loaiHangDAO.loadMenu();
		List<HangInfo> hangTivi = hangDAO.loadHangTheoLoai(1);
		List<HangInfo> hangTaiNghe = hangDAO.loadHangTheoLoai(2);
		List<HangInfo> hangDienThoai = hangDAO.loadHangTheoLoai(3);
		List<HangInfo> hangMayTinh = hangDAO.loadHangTheoLoai(4);
		List<HangInfo> hangTuLanh = hangDAO.loadHangTheoLoai(7);
		List<HangInfo> hangMayQuat = hangDAO.loadHangTheoLoai(9);
		List<HangInfo> hangLG = hangDAO.timKiemHangTheoTen("LG");
		List<HangInfo> hangSAMSUNG = hangDAO.timKiemHangTheoTen("SAMSUNG");
		List<HangInfo> hangIPHONE = hangDAO.timKiemHangTheoTen("IPHONE");
			
		model.addAttribute("tivi", hangTivi);
		model.addAttribute("tainghe", hangTaiNghe);
		model.addAttribute("dienthoai", hangDienThoai);
		model.addAttribute("maytinh", hangMayTinh);
		model.addAttribute("tulanh", hangTuLanh);
		model.addAttribute("mayquat", hangMayQuat);
		model.addAttribute("hang", hang);
		model.addAttribute("loaiHang", loaiHang);
		model.addAttribute("hangLG", hangLG);
		model.addAttribute("hangSAMSUNG", hangSAMSUNG);
		model.addAttribute("hangIPHONE", hangIPHONE);
		return "Index";
	}
	@RequestMapping("/cart")
	public String indexCart() {
		return "cart";
	}
	@RequestMapping("/chitiet")
	public String chiTietHang(Model model, HttpServletRequest request) {
		int maHang=Integer.parseInt(request.getParameter("id"));
		HangInfo hang = hangDAO.loadHangTheoId(maHang);
		model.addAttribute("hang_chitiet", hang);
		return "ChiTietHang";
	}
	//cách khác
	/*
	 * @RequestMapping("/chitiet") public String chiTietHang1(Model model,
	 * RequestParam("id") int maHang) {
	 * 
	 * // lấy đc id rồi đó. // có maHang roi ong viet cl sql là ok. return
	 * "ChiTietHang"; }
	 */
	@RequestMapping("/thanhtoan")
	public String thanhToan(Model model) {
		return "ThanhToan";
	}
	@RequestMapping(value = "/timkiem", method = RequestMethod.GET)
	public String timKiem(Model model,HttpServletRequest request, HttpSession session) {
		String tukhoa=request.getParameter("tukhoa");
		List<HangInfo> tkh = hangDAO.timKiemHangTheoTen(tukhoa);
		model.addAttribute("tkh", tkh);
		return "TimKiem";
	}
	@RequestMapping(value = "/thanhtoan/hoantat", method = RequestMethod.POST)
	public String hoanTat(Model model,HttpServletRequest request, HttpSession session) {
		int maUser;
		Random rand = new Random();
		int maDonHang =rand.nextInt(1000);
		List<GioHangInfo> gH=(List<GioHangInfo>) session.getAttribute("cart");
		float tt= 0;
		for (GioHangInfo hg : gH) {
			tt=tt+(hg.getSoLuong()*hg.getHang().getDonGia());
		}
		if (session.getAttribute("checkUser") == null) {
			maUser=33;
		}else {
			UserInfo user=(UserInfo)session.getAttribute("checkUser");
			maUser=user.getId();
		}
		String ngayDatHang=getToday();
		float tongTien=tt;
		String tenNguoiNhan=request.getParameter("tennguoinhan");
		String email=request.getParameter("email");
		String tinh=request.getParameter("tinhthanhpho");
		String huyen=request.getParameter("quanhuyen");
		String xa=request.getParameter("phuongxa");
		String diaChiNhan=request.getParameter("diachinhan")+"-"+xa+"-"+huyen+"-"+tinh;
		String sDT=request.getParameter("sdt");
		String ghiChu=request.getParameter("ghichu");
		int trangThai=4;
		int id=maUser;
		DonHangInfo donhang=new DonHangInfo(maDonHang,ngayDatHang,tongTien,tenNguoiNhan,email,diaChiNhan,sDT,ghiChu,trangThai,id);
		donHangDAO.insertDH(donhang);
		DonHangInfo dh=donHangDAO.loadDonHangDT(maUser, 4);
		for (GioHangInfo hg1 : gH) {
			int maDongDonHang =rand.nextInt(1000);
			DongDonHangInfo dongdonhang=new DongDonHangInfo(maDongDonHang,hg1.getSoLuong(),hg1.getHang().getMaHang(),dh.getMaDH());
			dongDonHangDAO.insertDH(dongdonhang);
			HangInfo hang=hangDAO.loadHangTheoId(hg1.getHang().getMaHang());
			HangInfo hangTT=new HangInfo(hang.getMaHang(),hang.getTenHang(),hang.getDonGia(),hang.getImageLink(),hang.getvAT(),hang.getMaLoai(),hang.getNhaSX(),hang.getNgaySX(),hang.gettGBaoHanh(),hang.gettTThem(),hang.getSoLuongHang()-hg1.getSoLuong(),hang.getTrangThaiHang());
			hangDAO.uploadHang(hangTT);
		}
		donHangDAO.updateTrangThaiDH(dh, 0);
		session.removeAttribute("cart");
		return "redirect:/";
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPage(Model model, @RequestParam String userName,
			@RequestParam String passWord, HttpSession session) {
		String request = "";
		UserInfo us= userDAO.checkLogin(userName,passWord);
		if(us !=null) {
			String loai=us.getLoai();
			if(loai.equals("0")) {
				session.setAttribute("checkUser", us);
				request = "redirect:/admin";	
			} else {
				session.setAttribute("checkUser",us);
				request = "redirect:/";
			}
		}
		else {
			session.setAttribute("loginF","Tên đăng nhập và mật khẩu sai");
			request="redirect:/";
		}
		return request;
	}
	@RequestMapping(value = "/buy/{id}", method = RequestMethod.GET)
	public String buy(@PathVariable("id") int id, HttpSession session) {
		if (session.getAttribute("cart") == null) {
			List<GioHangInfo> cart = new ArrayList<GioHangInfo>();
			cart.add(new GioHangInfo(hangDAO.loadHangTheoId(id), 1));
			session.setAttribute("cart", cart);
		} else {
			List<GioHangInfo> cart = (List<GioHangInfo>) session.getAttribute("cart");
			int index = this.exists(id, cart);
			if (index == -1) {
				cart.add(new GioHangInfo(hangDAO.loadHangTheoId(id), 1));
			} else {
				int quantity = cart.get(index).getSoLuong() + 1;
				cart.get(index).setSoLuong(quantity);
			}
			session.setAttribute("cart", cart);
		}
		return "redirect:/cart";
	}
	@RequestMapping(value = "/minus/{id}", method = RequestMethod.GET)
	public String minus(@PathVariable("id") int id, HttpSession session) {
			List<GioHangInfo> cart = (List<GioHangInfo>) session.getAttribute("cart");
			int index = this.exists(id, cart);
			if(cart.get(index).getSoLuong()>1) {
				int quantity = cart.get(index).getSoLuong() - 1;
				cart.get(index).setSoLuong(quantity);
			}
			session.setAttribute("cart", cart);
		return "redirect:/cart";
	}
	@RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
	public String remove(@PathVariable("id") int id, HttpSession session) {
		List<GioHangInfo> cart = (List<GioHangInfo>) session.getAttribute("cart");
		int index = this.exists(id, cart);
		cart.remove(index);
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}
	private int exists(int id, List<GioHangInfo> cart) {
		for (int i = 0; i < cart.size(); i++) {
			if (cart.get(i).getHang().getMaHang() == id) {
				return i;
			}
		}
		return -1;
	}
	public static String getToday() {
		cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
	}

}
