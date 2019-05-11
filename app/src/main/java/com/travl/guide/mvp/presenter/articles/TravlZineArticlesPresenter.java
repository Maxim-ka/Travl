package com.travl.guide.mvp.presenter.articles;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.travl.guide.mvp.model.api.articles.ArticleLink;
import com.travl.guide.mvp.model.api.articles.Author;
import com.travl.guide.mvp.model.api.articles.Category;
import com.travl.guide.mvp.model.repo.ArticlesRepo;
import com.travl.guide.mvp.presenter.articles.list.TravlZineArticlesListPresenter;
import com.travl.guide.mvp.view.articles.TravlZineArticlesView;
import com.travl.guide.mvp.view.articles.list.TravlZineArticlesItemView;
import com.travl.guide.mvp.view.articles.list.TravlZineFooterItemView;
import com.travl.guide.navigator.Screens;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import ru.terrakok.cicerone.Router;
import timber.log.Timber;

@InjectViewState
public class TravlZineArticlesPresenter extends MvpPresenter<TravlZineArticlesView> {

    private List<Disposable> disposables;

    public TravlZineArticlesListPresenter travlZineArticlesListPresenter;
    @Inject
    Router router;
    @Inject
    ArticlesRepo repo;
    @Inject
    @Named("baseUrl")
    String baseUrl;
    private Scheduler scheduler;

    public TravlZineArticlesPresenter(Scheduler scheduler) {
        this.scheduler = scheduler;
        travlZineArticlesListPresenter = new TravlZineArticlesListPresenterImpl();
        disposables = new ArrayList<>();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @SuppressLint("CheckResult")
    public void loadArticles() {
        Timber.e("Loading articles");
        disposables.add(repo.getTravlZineArticles().observeOn(scheduler).subscribe(articles -> travlZineArticlesListPresenter.setArticleLinkList(articles.getArticleLinkList()), Timber::e));
    }

    public void onDispose() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    public class TravlZineArticlesListPresenterImpl implements TravlZineArticlesListPresenter {

        private boolean footerBinded = false;

        private PublishSubject<TravlZineFooterItemView> footerItemViewPublishSubject = PublishSubject.create();

        @Override
        public PublishSubject<TravlZineFooterItemView> getFooterClickSubject() {
            return footerItemViewPublishSubject;
        }

        List<PublishSubject<TravlZineArticlesItemView>> publishSubjectList;
        private List<ArticleLink> articleLinkList;

        private void createPublishSubjects() {
            Timber.e("Creating publish subjects");
            int elementsToUpdate = 0;
            if (publishSubjectList == null) {
                publishSubjectList = new ArrayList<>();
                elementsToUpdate = getListCount();
            } else {
                elementsToUpdate = getListCount() - publishSubjectList.size();
            }
            for (int i = 0; i < elementsToUpdate; i++) {
                publishSubjectList.add(PublishSubject.create());
            }
        }

        @Override
        public PublishSubject<TravlZineArticlesItemView> getClickSubject(int position) {
            return publishSubjectList.get(position);
        }

        @SuppressLint("CheckResult")
        @Override
        public void bindView(TravlZineArticlesItemView view) {
            Timber.e("BindView and set Description");
            int position = view.getPos();
            ArticleLink articleLink = articleLinkList.get(position);
            disposables.add(publishSubjectList.get(position).subscribe(travlZineArticlesItemView -> router.navigateTo(new Screens.ArticleScreen(articleLink.getLink())), Timber::e));
            String title = articleLink.getTitle();
            String imageUrl = articleLink.getImageCoverUrl();
            List<Category> categories = articleLink.getCategories();
            Category category = null;
            String categoryName = null;
            if (categories != null) category = categories.get(0);
            if (title != null) view.setDescription(title);
            if (imageUrl != null)
                view.setImage(baseUrl + imageUrl.substring(1));
            if (category != null && (categoryName = category.getName()) != null)
                view.setCategory(categoryName);
            Author author = articleLink.getAuthor();
            String authorName = null;
            if (author != null && (authorName = author.getUserName()) != null)
                view.setAuthor(authorName);
        }

        @Override
        public void bindFooterView() {
            Timber.e("BindFooterView");
            if (!footerBinded) {
                disposables.add(footerItemViewPublishSubject.subscribe(TravlZineFooterItemView::loadMoreArticles, Timber::e));
                footerBinded = true;
            }
        }


        @Override
        public int getListCount() {
            return articleLinkList == null ? 0 : articleLinkList.size();
        }

        @Override
        public void setArticleLinkList(List<ArticleLink> articleLinks) {
            this.articleLinkList = articleLinks;
            if (articleLinkList == null || articleLinkList.size() == 0) {
                articleLinkList = new ArrayList<>();
                articleLinkList.add(new ArticleLink("Проверьте соединение", null));
            }
            createPublishSubjects();
            getViewState().onChangedArticlesData();
        }

        public void addArticles(List<ArticleLink> articleLinks) {
            //TODO: implement load of more TravlZine articles when API is ready
        }

        @Override
        public void loadMoreArticles() {
            //disposables.add(repo.getMoreTravlZineArticles().observeOn(scheduler).subscribe(articles -> travlZineArticlesListPresenter.addArticles(articles.getArticleLinkList()), Timber::e));
        }
    }
}