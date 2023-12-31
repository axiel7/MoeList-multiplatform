package data.model.media

import data.model.anime.Ranking

abstract class BaseRanking {
    abstract val node: BaseMediaNode
    abstract val ranking: Ranking?
    abstract val rankingType: RankingType?
}