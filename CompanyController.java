
package com.example.consumer;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


	

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	
	@Autowired
	private CompanyService companyService;	

	@GetMapping("/{ticker}")
    public Company getCompany(@PathVariable("ticker") String ticker) {       
		return companyService.getByTicker(ticker);        
    }	
	
	@GetMapping("/tickers")
    public Set<String> getTickerList() {       
		return companyService.getAllTickers();        
    }
	
	@GetMapping("/seed")
    public String populateDb() {		
		return companyService.seedDb();
    }
}



/*

package com.example.consumer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
@RequestMapping("/company")
public class CompanyController {
	
	
	@Autowired
	private CompanyService companyService;


	private List<String> list;
	private List<Company> obj;


	@GetMapping("/{ticker}")
    public Company getByTicker(@PathVariable("ticker") String ticker){


       return companyService.getByTicker(ticker);

    public Company getByTicker(@PathVariable("ticker") String ticker){ 
       return companyService.getByTicker(ticker);      
    }


}
	@GetMapping("/ticker")
	public List<String> getAllTicker(){	
		 list = companyService.getAllTickers();
			return companyService.getAllTickers();
		}


	@GetMapping("/ticker/stock")
	public List<Company> getCompanyFromTicker(List<String>list){

		return this.companyService.getCompanyFromTicker(list = companyService.getAllTickers());		

		}

	@GetMapping("/sector")
	public List<String> getAllSector(){	
		 list = companyService.getAllSectors();
			return companyService.getAllSectors();
		}

}	*/
