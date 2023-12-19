package net.corda.iou.api

import net.corda.client.rpc.notUsed
import net.corda.contracts.asset.Cash
import net.corda.core.contracts.Amount
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.getOrThrow
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.random63BitValue
import net.corda.core.serialization.makeNoWhitelistClassResolver
import net.corda.iou.flow.*
import net.corda.iou.state.IOUState
import org.apache.commons.math.random.RandomData
import org.bouncycastle.asn1.x500.X500Name
import org.jetbrains.exposed.sql.Date
import org.jetbrains.exposed.sql.dateParam
import rx.Observable
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.LocalDateTime

val SERVICE_NODE_NAMES = listOf(
        X500Name("CN=Controller,O=R3,L=London,C=UK"),
        X500Name("CN=NetworkMapService,O=R3,L=London,C=UK"))

/**
 * This API is accessible from /api/iou. The endpoint paths specified below are relative to it.
 * We've defined a bunch of endpoints to deal with IOUs, cash and the various operations you can perform with them.
 */
@Path("iou")
class IOUApi(val services: CordaRPCOps) {
    private val myLegalName = services.nodeIdentity().legalIdentity.name

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    fun whoami() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeers(): Map<String, List<X500Name>> {
        val (nodeInfo, nodeUpdates) = services.networkMapUpdates()
        nodeUpdates.notUsed()
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentity.name }
                .filter { it != myLegalName && it !in SERVICE_NODE_NAMES })
    }

    /**
     * Displays all IOU states that exist in the node's vault.
     */
    @GET
    @Path("ious")
    @Produces(MediaType.APPLICATION_JSON)
    // Filter by state type: IOU.
    fun getIOUs(): List<StateAndRef<ContractState>> {
        return services.vaultAndUpdates().justSnapshot.filter { it.state.data is IOUState }
    }

    @GET
    @Path("kyc")
    fun updateKYC(@QueryParam(value = "id") id: String,
                  @QueryParam(value = "kycstatus") kycstatus: String): Response {
        val linearId = UniqueIdentifier.fromString(id)
          val (status, message) = try {
            val flowHandle = services.startTrackedFlowDynamic(IOUTransferFlow.Initiator::class.java, linearId,kycstatus)
            flowHandle.use { flowHandle.returnValue.getOrThrow() }
            Response.Status.CREATED to "KYC updated for id $id."
        } catch (e: Exception) {
            Response.Status.BAD_REQUEST to e.message
        }

        return Response.status(status).entity(message).build()
    }
    /**
     * Displays all cash states that exist in the node's vault.
     */
    /*
    @GET
    @Path("cash")
    @Produces(MediaType.APPLICATION_JSON)
    // Filter by state type: Cash.
    fun getCash(): List<StateAndRef<ContractState>> {
        return services.vaultAndUpdates().justSnapshot.filter { it.state.data is Cash.State }
    }
*/
    /**
     * Displays all cash states that exist in the node's vault.
     */
    /*
    @GET
    @Path("cash-balances")
    @Produces(MediaType.APPLICATION_JSON)
    // Display cash balances.
    fun getCashBalances(): Map<Currency, Amount<Currency>> = services.getCashBalances()
*/
    /**
     * Initiates a flow to agree an IOU between two parties.
     *  val lender: Party, //Investor
    val borrower: Party, //Transfer Agent
    val fundManager: Party,
    val transactionID: Int,
    val transactionDate: Date,
    val transactionSettlementDate: Date,
    val fundId: String,
    val investorId: String,
    val nav: Float,
    val transactionAmount: Float,
    val units: Float,
    val kycValidated: Boolean,
    val txnStatus: String,
    val ccy: String,
    val amountPaid: Float,
     */
   /* fun Random(): Int{
        val i=50001
        val a=i+1
        return a;
         } */
   /* val random = Random()

    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }*/
    @GET
    @Path("issue-iou")
    fun issueIOU(
                 @QueryParam(value = "fundId") fundId: String,
                 @QueryParam(value = "txType") txType: String,
                 @QueryParam(value = "txId") txId: Int,
                 @QueryParam(value = "transactionAmount") transactionAmount: Int
                // @QueryParam(value = "Inverstor") investor1: String,
                // @QueryParam(value = "FundMgrA") fManager1: String,
                 //@QueryParam(value = "HSSTA") tAgent1: String
               ): Response {

        val tAgent1= "CN=TA,O=NodeA";
        val fManager1 = "CN=FM,O=NodeB";
        val tAgent = services.partyFromName(tAgent1) ?: throw IllegalArgumentException("Unknown party name.")
        val fManager = services.partyFromName(fManager1) ?: throw IllegalArgumentException("Unknown party name.")
        /*CN=InvestorPeter,O=NodeC
        CN=InvestorJohn,O=NodeD*/

       // val txnId = (Math.random()*20000).toInt();

        val txnId = 1009;
        //val tDate = Date();
        //val tsDate = Date();
        val investorId = "UH00001";
        val nav=  0.0f;
        val units= 0.0f;
        val kycValid= "no";
        val txStat= "PEND"
        val ccy= "GBP" ;//Need to pull this based on fund ID
        val amtPaid= 0.0f;
      //  val investor1 =
        val investor1 = "CN=Investor1,O=NodeC";
        //val investor1 = {{demoApp.thisNode}};
        val investor = services.partyFromName(investor1) ?: throw IllegalArgumentException("Unknown party name.")

        // Get party objects for myself and the counterparty.
        val me = services.nodeIdentity().legalIdentity
        //val trfAgent = services.partyFromName(tAgent) ?: throw IllegalArgumentException("Unknown party name.")
        //val fundMan = services.partyFromName(fManager) ?: throw IllegalArgumentException("Unknown party name.")
        // Create a new IOU state using the parameters given.
        val state = IOUState(fundId,txType,transactionAmount,tAgent,fManager,txnId,investorId,nav,units,kycValid,txStat,ccy,amtPaid,investor,LocalDateTime.now())
                //trfAgent, fundMan, txnId,tDate,tsDate,fundId,investorId,nav,txnAmt,units,kycValid, txStat,ccy,amtPaid)

        // Start the IOUIssueFlow. We block and wait for the flow to return.
        val (status, message) = try {
            val flowHandle = services.startTrackedFlowDynamic(IOUIssueFlow.Initiator::class.java, state, tAgent)
            val result = flowHandle.use { it.returnValue.getOrThrow() }
            // Return the response.
            Response.Status.CREATED to "Trade with id ${result.id} Created Successfully"
        } catch (e: Exception) {
            // For the purposes of this demo app, we do not differentiate by exception type.
            Response.Status.BAD_REQUEST to e.message
        }

        return Response.status(status).entity(message).build()
    }

    /**
     * tranfers an IOU specified by [linearId] to a new party.

    @GET
    @Path("transfer-iou")
    fun transferIOU(@QueryParam(value = "id") id: String,
                    @QueryParam(value = "party") party: String): Response {
        val linearId = UniqueIdentifier.fromString(id)
        val newLender = services.partyFromName(party) ?: throw IllegalArgumentException("Unknown party name.")

        val (status, message) = try {
            val flowHandle = services.startTrackedFlowDynamic(IOUTransferFlow.Initiator::class.java, linearId, newLender)
            // We don't care about the signed tx returned by the flow, only that it finishes successfully
            flowHandle.use { flowHandle.returnValue.getOrThrow() }
            Response.Status.CREATED to "IOU $id transferred to $party."
        } catch (e: Exception) {
            Response.Status.BAD_REQUEST to e.message
        }

        return Response.status(status).entity(message).build()
    }
*/
    /**
     * Settles an IOU. Requires cash in the right currency to be able to settle.*/

    @GET
    @Path("settle-iou")
    fun settleIOU(@QueryParam(value = "id") id: String,
                  @QueryParam(value = "amount") amount: Float): Response {
        val linearId = UniqueIdentifier.fromString(id)
        val settleAmount = amount
        System.out.print(settleAmount)
        val (status, message) = try {
            val flowHandle = services.startTrackedFlowDynamic(IOUSettleFlow.Initiator::class.java, linearId, settleAmount)
            flowHandle.use { flowHandle.returnValue.getOrThrow() }
            Response.Status.CREATED to "$amount paid off on IOU id $id."
        } catch (e: Exception) {
            Response.Status.BAD_REQUEST to e.message
        }

        return Response.status(status).entity(message).build()
    }

    /**
     * Helper end-point to issue some cash to ourselves.
*/
    @GET
    @Path("amountpaid")
    fun amountpaid(@QueryParam(value = "id") id: String,
            @QueryParam(value = "amount") amount: Float): Response {
        val issueAmount = amount
        val linearId = UniqueIdentifier.fromString(id)
        val (status, message) = try {
            val flowHandle = services.startTrackedFlowDynamic(AmountPaid.Initiator::class.java, linearId, issueAmount)
            flowHandle.use { flowHandle.returnValue.getOrThrow() }
            Response.Status.CREATED to "$amount paid off on IOU id $id."
        } catch (e: Exception) {
            Response.Status.BAD_REQUEST to e.message
        }

        return Response.status(status).entity(message).build()
    }
    // Helper method to get just the snapshot portion of an RPC call which also returns an Observable of updates. It's
    // important to unsubscribe from this Observable if we're not going to use it as otherwise we leak resources on the server.
    private val <A> Pair<A, Observable<*>>.justSnapshot: A get() {
        second.notUsed()
        return first
    }
}