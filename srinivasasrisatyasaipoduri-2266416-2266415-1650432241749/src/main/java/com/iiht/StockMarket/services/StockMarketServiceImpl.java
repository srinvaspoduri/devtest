package com.iiht.StockMarket.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiht.StockMarket.dto.StockPriceDetailsDTO;
import com.iiht.StockMarket.dto.StockPriceIndexDTO;
import com.iiht.StockMarket.model.CompanyDetails;
import com.iiht.StockMarket.model.StockPriceDetails;
import com.iiht.StockMarket.repository.CompanyInfoRepository;
import com.iiht.StockMarket.repository.StockPriceRepository;
import com.iiht.StockMarket.utils.StockMarketUtility;

@Service
public class StockMarketServiceImpl implements StockMarketService {

	@Autowired
    private StockPriceRepository stockRepository;

	@Autowired
    private CompanyInfoRepository companyRepository;

	//private long count;
	
	//----------------------------------------------------------------------------
	public StockPriceDetailsDTO saveStockPriceDetails(StockPriceDetailsDTO stockPriceDetailsDTO) {
        
   
    Long companyCode=stockPriceDetailsDTO.getCompanyCode();
		Optional<CompanyDetails>  companyDetails =companyRepository.findById(companyCode);
		if(companyDetails.isPresent()) {
			StockPriceDetails stockPriceDetails= stockRepository.save(StockMarketUtility.convertToStockPriceDetails(stockPriceDetailsDTO));
			return StockMarketUtility.convertToStockPriceDetailsDTO(stockPriceDetails);
		}
		else
		return  null;
	};
	//----------------------------------------------------------------------------
	public List<StockPriceDetailsDTO> deleteStock(Long companyCode) {
		
        List<StockPriceDetailsDTO> lPriceDetailsDTOs = getStockByCode(companyCode);

        Stream<StockPriceDetailsDTO> streamstockPriceDetailsDTO= lPriceDetailsDTOs.stream();
        Integer count = (int) streamstockPriceDetailsDTO
                .map((x) -> stockRepository.deleteByCompanyCode(x.getCompanyCode())).count();
            streamstockPriceDetailsDTO.close();
              if(count==lPriceDetailsDTOs.size())
        return lPriceDetailsDTOs;
        else
       return null;
     }
	//----------------------------------------------------------------------------
	public List<StockPriceDetailsDTO> getStockByCode(Long companyCode){

List<StockPriceDetailsDTO> stockPriceDetailsDTO = StockMarketUtility.convertToStockPriceDetailsDtoList(stockRepository.findStockPriceDetailsById(companyCode));
     
      return stockPriceDetailsDTO;
      
		
	};
	//----------------------------------------------------------------------------
	public StockPriceDetailsDTO getStockPriceDetailsDTO(StockPriceDetails stockDetails)	{

      return  StockMarketUtility.convertToStockPriceDetailsDTO(stockDetails);
		
	};	
	//----------------------------------------------------------------------------
	public Double getMaxStockPrice(Long companyCode, LocalDate startDate, LocalDate endDate) {
	return stockRepository.findMaxStockPrice(companyCode, startDate, endDate);
	};
	public Double getAvgStockPrice(Long companyCode, LocalDate startDate, LocalDate endDate) {
		return stockRepository.findAvgStockPrice(companyCode, startDate, endDate);
	};
	public Double getMinStockPrice(Long companyCode, LocalDate startDate, LocalDate endDate) {
	 return stockRepository.findMinStockPrice(companyCode, startDate, endDate);
	};
	
	public StockPriceIndexDTO getStockPriceIndex(Long companyCode, LocalDate startDate, LocalDate endDate) {
		
	     CompanyDetails companyDetails=companyRepository.findCompanyDetailsById(companyCode);
        List<StockPriceDetails> stockPriceDetailList=stockRepository.findStockByCompanyCodeBetweendates(companyCode,startDate,endDate);
        
        Double maxStockPrice=getMaxStockPrice(companyCode,startDate,endDate);
        Double avgStockPrice=getAvgStockPrice(companyCode,startDate,endDate);
        Double minStockPrice=getMinStockPrice(companyCode,startDate,endDate);
        if(companyDetails!=null)
        {
            StockPriceIndexDTO stockPriceIndexDto=new StockPriceIndexDTO();
            stockPriceIndexDto.setCompanyDto(StockMarketUtility.convertToCompanyDetailsDTO(companyDetails));
            if(stockPriceDetailList!=null)
            {
                stockPriceIndexDto.setAvgStockPrice(avgStockPrice);
                stockPriceIndexDto.setMaxStockPrice(maxStockPrice);
                stockPriceIndexDto.setMinStockPrice(minStockPrice);
                stockPriceIndexDto.setStockPriceList(StockMarketUtility.convertToStockPriceDetailsDtoList(stockPriceDetailList));            
            }
            return stockPriceIndexDto;
        }
        
		return null;
	}
}