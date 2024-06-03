package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.CartBean;
import model.CartModel;
import model.PreferitiModel;
import model.ProductBean;
import model.ProductModel;

@WebServlet("/ProductControl")
public class ProductControl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ProductControl.class.getName());

    static ProductModel model;

    static {
        model = new ProductModel();
    }

    public ProductControl() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if (action != null && action.equals("dettaglio")) {
                handleDettaglio(request, response);
            } else if (action != null && action.equals("elimina")) {
                handleElimina(request, response);
            } else if (action != null && action.equals("modificaForm")) {
                handleModificaForm(request, response);
            } else if (action != null && action.equals("modifica")) {
                handleModifica(request, response);
            } else {
                handleDefault(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing request", e);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void handleDettaglio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            String codiceStr = request.getParameter("codice");
            int codice = Integer.parseInt(codiceStr);
            ProductBean prodotto = model.doRetrieveByKey(codice);
            request.setAttribute("prodottoDettaglio", prodotto);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/productDetail.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            logger.log(Level.SEVERE, "Error in handleDettaglio", e);
            throw e;
        }
    }

    private void handleElimina(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            @SuppressWarnings("unchecked")
            Collection<ProductBean> lista = (Collection<ProductBean>) request.getSession().getAttribute("products");
            int codice = Integer.parseInt(request.getParameter("codice"));
            Collection<ProductBean> collezione = model.deleteProduct(codice, lista);

            request.getSession().removeAttribute("products");
            request.getSession().setAttribute("products", collezione);
            request.getSession().setAttribute("refreshProduct", true);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ProductsPage.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error in handleElimina", e);
            throw e;
        }
    }

    private void handleModificaForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            ProductBean bean = model.doRetrieveByKey(Integer.parseInt(request.getParameter("codice")));
            request.setAttribute("updateProd", bean);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/modifica-prodotto.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            logger.log(Level.SEVERE, "Error in handleModificaForm", e);
            throw e;
        }
    }

    private void handleModifica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            ProductBean bean = new ProductBean();
            bean.setCodice(Integer.parseInt(request.getParameter("codice")));
            bean.setNome(request.getParameter("nome"));
            bean.setDescrizione(request.getParameter("descrizione"));
            bean.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
            bean.setSpedizione(Double.parseDouble(request.getParameter("spedizione")));
            bean.setTag(request.getParameter("tag"));
            bean.setTipologia(request.getParameter("tipologia"));

            model.updateProduct(bean);
            if (request.getSession().getAttribute("carrello") != null) {
                CartModel cartmodel = new CartModel();
                CartBean newCart = cartmodel.updateCarrello(bean, (CartBean) request.getSession().getAttribute("carrello"));
                request.getSession().setAttribute("carrello", newCart);
            }
            if (request.getSession().getAttribute("preferiti") != null) {
                PreferitiModel preferitiModel = new PreferitiModel();
                @SuppressWarnings("unchecked")
                Collection<ProductBean> lista = preferitiModel.updatePreferiti(bean, (Collection<ProductBean>) request.getSession().getAttribute("preferiti"));
                request.getSession().setAttribute("preferiti", lista);
            }

            request.getSession().setAttribute("refreshProduct", true);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error in handleModifica", e);
            throw e;
        }
    }

    private void handleDefault(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            String tipologia = (String) request.getSession().getAttribute("tipologia");
            request.removeAttribute("products");
            request.setAttribute("products", model.doRetrieveAll(tipologia));
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ProductsPage.jsp?tipologia=" + tipologia);
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error in handleDefault", e);
            throw e;
        }
    }
}
