import { Link } from "react-router-dom";
import AuctionImage from "./AuctionImage";
import StatusBadge from "./StatusBadge";
import Countdown from "./Countdown";
import { money } from "../utils/format";

export default function AuctionCard({ auction }) {
  const live = auction.status === "ACTIVE";
  const timeLabel =
    auction.status === "SCHEDULED" ? "Starts in" : auction.status === "ENDED" ? "" : "Time left";

  return (
    <article className={`auction-card ${live ? "live" : ""}`}>
      <div className="card-media">
        <AuctionImage src={auction.imageUrl} title={auction.title} />
        <div className="card-badge">
          <StatusBadge status={auction.status} />
        </div>
      </div>

      <div className="card-body">
        <h3 className="card-title">{auction.title}</h3>

        <div className="card-metrics">
          <div>
            <div className="metric-label">Current bid</div>
            <div className="metric-value cyan">
              {auction.currentHighestBid ? money(auction.currentHighestBid) : "No bids"}
            </div>
          </div>
          <div>
            <div className="metric-label">Starting price</div>
            <div className="metric-value">{money(auction.startingPrice)}</div>
          </div>
        </div>

        <div className="card-foot">
          <span>{auction.bidCount ?? 0} bids</span>
          <span className="foot-time">
            {timeLabel && <small>{timeLabel}</small>}
            <Countdown status={auction.status} startTime={auction.startTime} endTime={auction.endTime} />
          </span>
        </div>

        <Link className="btn btn-primary btn-block" to={`/auctions/${auction.id}`}>
          View Auction
        </Link>
      </div>
    </article>
  );
}