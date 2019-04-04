package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookKeeperTest {

    private InvoiceRequest invoiceRequest;
    private ClientData clientData;
    private RequestItem requestItemFirst;
    private RequestItem requestItemSecond;
    private BookKeeper bookKeeper;
    private TaxPolicy taxPolicy;



    private void init(){
        clientData = new ClientData(new Id("1"),"Oskar");
        requestItemFirst = new RequestItem(new ProductData(new Id("2"),new Money(123),"123", ProductType.DRUG,new Date()),1,new Money(1));
        requestItemSecond = new RequestItem(new ProductData(new Id("2"),new Money(123),"123", ProductType.DRUG,new Date()),2,new Money(1));
        bookKeeper = new BookKeeper(new InvoiceFactory());
        taxPolicy = Mockito.mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(1), "test"));
    }
    public BookKeeperTest(){
        init();
    }
    @Test
    public void Check_issuance_is_returning_invoice_with_one_position_when_request_is_for_invoice_with_one_position(){
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItemFirst);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size()).isEqualTo(1);

    }

    @Test
    public void Check_issuance_is_calling_calculateTaxMethod_2_times_when_there_is_2_items_in_request(){
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItemFirst);
        invoiceRequest.add(requestItemSecond);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, Mockito.times(2)).calculateTax(any(ProductType.class), any(Money.class));


    }

    @Test
    public void Check_issuance_is_not_calling_calculateTaxMethod_when_there_is_no_items_in_request(){
        invoiceRequest = new InvoiceRequest(clientData);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, Mockito.times(0)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void Check_issuance_is_getting_client_data_when_client_data_is_definied_in_request(){
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItemFirst);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getClient().getName()).isEqualTo("Oskar");
    }

    @Test
    public void Check_issuance_is_returning_invoice_with_zero_items_when_there_is_zero_items_in_request(){
        invoiceRequest = new InvoiceRequest(clientData);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size()).isEqualTo(0);
    }

    @Test
    public void Check_issuance_is_calling_factorymethod_when_there_is_zero_items_in_request(){
        InvoiceFactory invoiceFactory = new Mockito().mock(InvoiceFactory.class);
        when(invoiceFactory.create(any(ClientData.class))).thenReturn(new Invoice(new Id("1"),clientData));
        invoiceRequest = new InvoiceRequest(clientData);
        verify(invoiceFactory,Mockito.times(0)).create(any(ClientData.class));
    }
}
