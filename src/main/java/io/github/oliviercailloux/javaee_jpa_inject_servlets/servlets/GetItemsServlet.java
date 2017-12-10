package io.github.oliviercailloux.javaee_jpa_inject_servlets.servlets;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.oliviercailloux.javaee_jpa_inject_servlets.model.Item;
import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.QueryHelper;
import io.github.oliviercailloux.javaee_jpa_inject_servlets.utils.ServletHelper;

@WebServlet("/getItemsServlet")
public class GetItemsServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		@SuppressWarnings("resource")
		final ServletOutputStream out = new ServletHelper().configureAndGetOutputStream(resp);
		out.println("I use an application-managed resource-local entity manager.");
		out.println("My persistence context is transaction-scoped (lifetime scoped to a single transaction).");
		out.flush();

		final EntityManagerFactory emFactory = Persistence
				.createEntityManagerFactory("JavaEE-JPA-resource-local-Servlets");
		final EntityManager em = emFactory.createEntityManager();

		final QueryHelper queryHelper = new QueryHelper();
		queryHelper.setEmFactory(emFactory);

		final CriteriaQuery<Item> selectAll = queryHelper.selectAll(Item.class);
		final EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		final List<Item> allItems = em.createQuery(selectAll).getResultList();
		transaction.commit();

		for (Item item : allItems) {
			out.println(item.getName());
		}
		out.println("End.");

		em.close();
	}
}
